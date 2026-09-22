package com.example.authservice.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record UserResponseDTO(UUID id, String username, List<String> roles) {
}
