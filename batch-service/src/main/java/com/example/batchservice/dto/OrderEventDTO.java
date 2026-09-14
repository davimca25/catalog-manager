package com.example.batchservice.dto;

import com.example.batchservice.model.Status;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderEventDTO(
        UUID orderId,
        String userName,
        Status status,
        LocalDateTime createdAt,
        List<OrderItemEventDTO> items
) implements Serializable {
    public record OrderItemEventDTO(
            UUID productId,
            Integer quantity,
            BigDecimal price

    ) implements Serializable{}
}