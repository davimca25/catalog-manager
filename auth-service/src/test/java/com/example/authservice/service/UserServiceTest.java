package com.example.authservice.service;

import com.example.authservice.dto.UserRequestDTO;
import com.example.authservice.dto.UserResponseDTO;
import com.example.authservice.model.Role;
import com.example.authservice.model.RoleName;
import com.example.authservice.model.User;
import com.example.authservice.repository.RoleRepository;
import com.example.authservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    @Test
    void shouldCreateNewUser() {
        //arrange
        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setName(RoleName.ROLE_USER);
        roles.add(role);
        UUID userId = UUID.randomUUID();
        String userName = "Davi";
        String password = "12345";
        UserRequestDTO userRequestDTO = new UserRequestDTO(userName, password);

        when(passwordEncoder.encode(password)).thenReturn("encoded_password_12345");

        UserResponseDTO userResponseDTO = new UserResponseDTO(userId, userName, Collections.singletonList(String.valueOf(roles)));



        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(role));

        User user = new User(userName, password, roles);
        user.setId(userId);

        when(userRepository.save(any(User.class))).thenReturn(user);

        //act
        UserResponseDTO result = userService.register(userRequestDTO);

        //assert
        assertNotNull(result);
        assertEquals(userId, result.id());

    }
}