package com.backend.userservice.service;

import com.backend.userservice.model.Role;
import com.backend.userservice.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Set<Role> getDefaultRoles() {
        Set<Role> roles = new HashSet<>();
        // attribuer le rôle USER par défaut
        Optional<Role> userRole = roleRepository.findByName("USER");
        userRole.ifPresent(roles::add);
        return roles;
    }

    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }


}
