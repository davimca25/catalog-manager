package com.example.authservice.service;

import com.example.authservice.dto.UserRequestDTO;
import com.example.authservice.dto.UserResponseDTO;
import com.example.authservice.model.Role;
import com.example.authservice.model.RoleName;
import com.example.authservice.model.User;
import com.example.authservice.repository.RoleRepository;
import com.example.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
