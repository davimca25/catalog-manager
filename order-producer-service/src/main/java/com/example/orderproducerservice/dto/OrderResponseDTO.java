package com.example.orderproducerservice.dto;

import com.example.orderproducerservice.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponseDTO(UUID id, UUID userId, Status status, LocalDateTime createdAt, List<OrderItemRequestDTO> items) {
}
