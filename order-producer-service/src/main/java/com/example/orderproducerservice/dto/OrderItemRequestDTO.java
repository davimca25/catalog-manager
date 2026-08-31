package com.example.orderproducerservice.dto;

import java.util.UUID;

public record OrderItemRequestDTO(UUID productId, Integer quantity) {
}
