package com.example.batchservice.model;

import com.example.batchservice.dto.OrderEventDTO.OrderItemEventDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order_staging")
public class OrderBatchStaging {

    @Id
    private UUID orderId;

    private String userName;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;

    private List<OrderItemEventDTO> items;
}
