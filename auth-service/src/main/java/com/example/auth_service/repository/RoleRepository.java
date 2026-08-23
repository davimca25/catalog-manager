package com.example.auth_service.repository;

import com.example.auth_service.model.Role;
import com.example.auth_service.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(RoleName name);
}
