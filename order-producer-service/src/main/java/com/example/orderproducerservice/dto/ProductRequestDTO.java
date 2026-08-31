package com.example.orderproducerservice.dto;

import java.math.BigDecimal;

public record ProductRequestDTO(String name, BigDecimal price, Integer quantity) {
}
