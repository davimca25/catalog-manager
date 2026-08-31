package com.example.orderproducerservice.dto;

import java.util.List;

public record OrderRequestDTO(List<OrderItemRequestDTO> items) {
}
