package com.example.auth_service.service;

import com.example.auth_service.dto.UserRequestDTO;
import com.example.auth_service.dto.UserResponseDTO;
import com.example.auth_service.model.Role;
import com.example.auth_service.model.RoleName;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public UserResponseDTO register(UserRequestDTO request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException();
        }

        String passwordEncoded = encoder.encode(request.password());

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found."));

        User user = new User(request.username(), passwordEncoded, List.of(userRole));

        User savedUser = userRepository.save(user);

        return UserResponseDTO.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .roles(savedUser.getRoles().stream().map(role -> role.getName().name())
                        .toList())
                .build();
    }


}
