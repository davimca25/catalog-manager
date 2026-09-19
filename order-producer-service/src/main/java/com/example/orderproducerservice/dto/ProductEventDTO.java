package com.example.orderproducerservice.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductEventDTO(
        UUID Id,
        String name,
        BigDecimal price,
        Integer quantity,
        boolean active,
        Action action
) implements Serializable {}
