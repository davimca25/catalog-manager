package com.example.auth_service.config;

import com.example.auth_service.model.Role;
import com.example.auth_service.model.RoleName;
import com.example.auth_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotFound(RoleName.ROLE_USER);
        createRoleIfNotFound(RoleName.ROLE_ADMIN);
    }

    private void createRoleIfNotFound(RoleName roleName) {
        if(roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}
