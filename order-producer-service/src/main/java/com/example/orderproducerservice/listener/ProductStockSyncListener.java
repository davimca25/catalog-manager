package com.example.orderproducerservice.listener;

import com.example.orderproducerservice.dto.ProductStockSyncDTO;
import com.example.orderproducerservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductStockSyncListener {

    private final ProductRepository productRepository;

    @RabbitListener(queues = "${rabbitmq.queue.product.stock.name:product.stock.queue}")
    @Transactional
    public void receiveProductStockSyncEvent(List<ProductStockSyncDTO> products) {
        log.info("Product received from stock, {} items.", products.size());


    }
}
