package com.example.stockservice.listener;

import com.example.stockservice.dto.Action;
import com.example.stockservice.dto.ProductEventDTO;
import com.example.stockservice.model.Product;
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

    @RabbitListener(queues = "${rabbitmq.queue.product.name:product.queue}")
    @Transactional
    public void receiveNewProduct(ProductEventDTO productDTO) {
        switch (productDTO.action()) {
            case CREATE:

                    Product product1 = new Product(
                            productDTO.id(),
                            productDTO.name(),
                            productDTO.price(),
                            productDTO.quantity(),
                            true
                    );
                    productRepository.save(product1);

                    log.info("Product saved: ProductId = {}, ProductName = {}, ProductPrice = {}, ProductQuantity = {}",
                            product1.getId(),
                            product1.getName(),
                            product1.getPrice(),
                            product1.getQuantity()
                    );

                    break;

            case UPDATE:

                productRepository.findById(productDTO.id()).ifPresentOrElse(product -> {
                    product.setName(productDTO.name());
                    product.setPrice(productDTO.price());
                    product.setQuantity(productDTO.quantity());
                    productRepository.save(product);

                }, () -> log.error("ProductId = {} not found", productDTO.id()));

                break;

            case DELETE:

                productRepository.findById(productDTO.id()).ifPresentOrElse(product -> {
                    product.setActive(false);
                    productRepository.save(product);

                }, () -> log.error("ProductId = {} not found", productDTO.id()));

                break;


        }

    }
}
