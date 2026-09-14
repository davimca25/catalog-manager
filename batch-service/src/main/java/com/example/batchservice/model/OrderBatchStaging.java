package com.example.batchservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.annotation.Documented;
import java.time.LocalDateTime;
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
}
