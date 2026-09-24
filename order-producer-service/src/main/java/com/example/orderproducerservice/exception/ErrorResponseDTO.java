package com.example.orderproducerservice.exception;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        LocalDateTime timeStamp,
        int status,
        String error,
        String message,
        String path
) {}

