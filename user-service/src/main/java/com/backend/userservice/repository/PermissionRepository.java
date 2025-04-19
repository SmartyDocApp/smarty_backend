package com.backend.userservice.repository;

import com.backend.userservice.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PermissionRepository extends JpaRepository<Permission, String> {
    Optional<Permission> findByName(String name);
    boolean existsByName(String name);
    
}
