package com.example.stockservice.listener;

import com.example.stockservice.dto.OrderEventDTO;
import com.example.stockservice.dto.OrderEventDTO.OrderItemEventDTO;
import com.example.stockservice.model.Product;
import com.example.stockservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StockConsumerListener {

    private final ProductRepository productRepository;

    @RabbitListener(queues = "${rabbitmq.queue.stock.name:stock.queue}")
    @Transactional
    public void receiveStockEvent(OrderEventDTO orderEventDTO) {

        log.info("Message received from stack queue: OrderId = {}, UserName = {}", orderEventDTO.orderId(), orderEventDTO.userName());

        // para cada product em orderEventDTO devemos salvar no db
        for (OrderItemEventDTO orderItemEventDTO : orderEventDTO.items()) {
            productRepository.findById(orderItemEventDTO.productId());
        }

    }
}
