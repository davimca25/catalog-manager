package com.example.stockservice.listener;

import com.example.stockservice.dto.ProductEventDTO;
import com.example.stockservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCreatedListener {

    private final ProductRepository productRepository;

    @RabbitListener(queues = "")
    @Transactional
    public void receiveNewProduct(ProductEventDTO productDTO) {

    }
}
