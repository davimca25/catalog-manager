package com.example.auth_service.dto;

import com.example.auth_service.model.Role;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record UserResponseDTO(UUID id, String username, List<String> roles) {
}
