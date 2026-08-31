package com.example.orderproducerservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponseDTO(UUID id, String name, BigDecimal price, Integer quantity) {
}
