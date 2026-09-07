package com.example.orderproducerservice.service;

import com.example.orderproducerservice.dto.ProductRequestDTO;
import com.example.orderproducerservice.dto.ProductResponseDTO;
import com.example.orderproducerservice.model.Product;
import com.example.orderproducerservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = new Product(productRequestDTO.name(), productRequestDTO.price(), productRequestDTO.quantity());

        Product productSaved = productRepository.save(product);

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

    public ProductResponseDTO updateProduct(UUID id, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found."));

        product.setName(productRequestDTO.name());
        product.setPrice(productRequestDTO.price());
        product.setQuantity(productRequestDTO.quantity());

        Product productSaved = productRepository.save(product);

        return new ProductResponseDTO(productSaved.getId(),
                productSaved.getName(),
                productSaved.getPrice(),
                productSaved.getQuantity());

    }

    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found."));
        productRepository.delete(product);
    }
}
