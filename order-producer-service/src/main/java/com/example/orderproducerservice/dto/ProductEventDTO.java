package com.example.orderproducerservice.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductEventDTO(
        UUID id,
        String name,
        BigDecimal price,
        Integer quantity,
        boolean active,
        Action action
) implements Serializable {}
