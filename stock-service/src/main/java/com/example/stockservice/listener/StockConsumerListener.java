package com.example.stockservice.listener;

import com.example.stockservice.dto.OrderEventDTO;
import com.example.stockservice.dto.OrderEventDTO.OrderItemEventDTO;
import com.example.stockservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StockConsumerListener {

    private final ProductRepository productRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.order.response.name:order.response.exchange}")
    private String orderResponseExchange;

    @Value("${rabbitmq.routing.order.response.key:order.response.routing.key}")
    private String orderResponseRoutingKey;

    @RabbitListener(queues = "${rabbitmq.queue.stock.name:stock.queue}")
    @Transactional
    public void receiveStockEvent(OrderEventDTO orderEventDTO) {

        log.info("Message received from stock queue: OrderId = {}, UserName = {}",
                orderEventDTO.orderId(),
                orderEventDTO.userName()
        );

        for (OrderItemEventDTO orderItemEventDTO : orderEventDTO.items()) {

            productRepository.findById(orderItemEventDTO.productId()).ifPresentOrElse(product -> {
                int newQuantity = product.getQuantity() - orderItemEventDTO.quantity();

                if (newQuantity >= 0) {
                    product.setQuantity(newQuantity);
                    productRepository.save(product);

                } else {
                    log.warn("Insufficient stock for ProductId = {}. Current Stock = {}, Requested = {}",
                            product.getId(),
                            product.getQuantity(),
                            orderItemEventDTO.quantity()
                    );

                }

            }, () -> log.error("ProductId not found: ProductId = {}", orderItemEventDTO.productId()));
        }

    }
}
