package com.example.stockservice.listener;

import com.example.stockservice.dto.OrderEventDTO;
import com.example.stockservice.dto.OrderEventDTO.OrderItemEventDTO;
import com.example.stockservice.dto.ProductStockSyncDTO;
import com.example.stockservice.model.Status;
import com.example.stockservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

    @Value("${rabbitmq.exchange.product.stock.name:product.stock.exchange}")
    private String productStockExchange;

    @Value("${rabbitmq.routing.product.stock.key:product.stock.routing.key}")
    private String productStockRoutingKey;

    @RabbitListener(queues = "${rabbitmq.queue.stock.name:stock.queue}")
    @Transactional
    public void receiveStockEvent(OrderEventDTO orderEventDTO) {

        log.info("Message received from stock queue: OrderId = {}, UserName = {}",
                orderEventDTO.orderId(),
                orderEventDTO.userName()
        );

        AtomicBoolean hasStockError = new AtomicBoolean(false);

        List<ProductStockSyncDTO> productsStock = new ArrayList<>();

        for (OrderItemEventDTO orderItemEventDTO : orderEventDTO.items()) {

            productRepository.findById(orderItemEventDTO.productId()).ifPresentOrElse(product -> {
                int newQuantity = product.getQuantity() - orderItemEventDTO.quantity();

                if (newQuantity >= 0) {
                    product.setQuantity(newQuantity);
                    productRepository.save(product);

                    productsStock.add(new ProductStockSyncDTO(product.getId(), newQuantity));

                    log.info("Stock updated for ProductId = {}", product.getId());

                } else {
                    log.warn("Insufficient stock for ProductId = {}. Current Stock = {}, Requested = {}",
                            product.getId(),
                            product.getQuantity(),
                            orderItemEventDTO.quantity()
                    );
                    hasStockError.set(true);
                }

            }, () -> {
                log.error("ProductId not found: ProductId = {}", orderItemEventDTO.productId());
                hasStockError.set(true);
            });
        }
        if (!hasStockError.get()) {
            rabbitTemplate.convertAndSend(
                    productStockExchange,
                    productStockRoutingKey,
                    productsStock
            );
        }

        Status finalStatus = hasStockError.get() ? Status.FAILED : Status.COMPLETED;

        OrderEventDTO responseEvent = new OrderEventDTO(
                orderEventDTO.orderId(),
                orderEventDTO.userName(),
                finalStatus,
                orderEventDTO.createdAt(),
                orderEventDTO.items()
        );

        rabbitTemplate.convertAndSend(
                orderResponseExchange,
                orderResponseRoutingKey,
                responseEvent
        );
    }
}
