package com.example.orderproducerservice.controller;

import com.example.orderproducerservice.dto.ProductRequestDTO;
import com.example.orderproducerservice.dto.ProductResponseDTO;
import com.example.orderproducerservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> listAllProducts() {
        return ResponseEntity.ok().body(productService.listAllProducts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable("id") UUID id,
            @RequestBody ProductRequestDTO productRequestDTO
    ) {
        return ResponseEntity.ok().body(productService.updateProduct(id, productRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
