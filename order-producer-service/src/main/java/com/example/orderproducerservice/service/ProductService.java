package com.example.orderproducerservice.service;

import com.example.orderproducerservice.dto.Action;
import com.example.orderproducerservice.dto.ProductEventDTO;
import com.example.orderproducerservice.dto.ProductRequestDTO;
import com.example.orderproducerservice.dto.ProductResponseDTO;
import com.example.orderproducerservice.model.Product;
import com.example.orderproducerservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.product.name:product.exchange}")
    private String productExchange;

    @Value("${rabbitmq.routing.product.key:product.routing.key}")
    private String productRoutingKey;

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = new Product(
                productRequestDTO.name(),
                productRequestDTO.price(),
                productRequestDTO.quantity()
        );

        Product productSaved = productRepository.save(product);

        rabbitTemplate.convertAndSend(productExchange, productRoutingKey, new ProductEventDTO(
                productSaved.getId(),
                productSaved.getName(),
                productSaved.getPrice(),
                productSaved.getQuantity(),
                true,
                Action.CREATE
        ));

        return new ProductResponseDTO(productSaved.getId(),
                productSaved.getName(),
                productSaved.getPrice(),
                productSaved.getQuantity());
    }

    public List<ProductResponseDTO> listAllProducts() {
        List<Product> productsList = productRepository.findAll();

        return productsList.stream()
                .map(product -> new ProductResponseDTO(product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getQuantity()))
                .toList();
    }

    @Transactional
    public ProductResponseDTO updateProduct(UUID id, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found."));

        product.setName(productRequestDTO.name());
        product.setPrice(productRequestDTO.price());
        product.setQuantity(productRequestDTO.quantity());

        Product productSaved = productRepository.save(product);

        rabbitTemplate.convertAndSend(productExchange, productRoutingKey, new ProductEventDTO(
                productSaved.getId(),
                productSaved.getName(),
                productSaved.getPrice(),
                productSaved.getQuantity(),
                true,
                Action.UPDATE
        ));

        return new ProductResponseDTO(productSaved.getId(),
                productSaved.getName(),
                productSaved.getPrice(),
                productSaved.getQuantity());

    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found."));

        product.setActive(false);
        productRepository.save(product);

        rabbitTemplate.convertAndSend(productExchange, productRoutingKey, new ProductEventDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                false,
                Action.DELETE
                )
        );
    }
}
