package com.example.stockservice.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductEventDTO(
        UUID Id,
        String name,
        BigDecimal price,
        Integer quantity,
        Action action
) implements Serializable {}
