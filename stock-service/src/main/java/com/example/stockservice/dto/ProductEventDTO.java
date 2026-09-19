package com.example.stockservice.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductEventDTO(
        UUID id,
        String name,
        BigDecimal price,
        Integer quantity,
        Action action
) implements Serializable {}
