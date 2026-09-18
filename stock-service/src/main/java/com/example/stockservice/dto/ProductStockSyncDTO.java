package com.example.stockservice.dto;

import java.io.Serializable;
import java.util.UUID;

public record ProductStockSyncDTO(UUID productId, Integer newQuantity) implements Serializable {
}
