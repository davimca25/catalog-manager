package com.example.stockservice.listener;

import com.example.stockservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCreatedListener {

    private final ProductRepository productRepository;

    public void receiveNewProduct() {

    }
}
