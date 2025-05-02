package com.backend.userservice.service;


import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.userservice.dto.UserDto;
import com.backend.userservice.model.Role;
import com.backend.userservice.model.User;
import com.backend.userservice.repository.UserRepository;

@Service //
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Vérification du mot de passe haché
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Construction manuelle du UserDto
                UserDto dto = new UserDto();
                dto.setId(user.getId());
                dto.setUsername(user.getUsername());
                dto.setEmail(user.getEmail());
                dto.setFirstName(user.getFirstName());
                dto.setLastName(user.getLastName());
                dto.setCreatedAt(user.getCreatedAt());
                dto.setUpdatedAt(user.getUpdatedAt());
                dto.setLastLogin(user.getLastLogin());
                dto.setEnabled(user.isEnabled());
                // Conversion des rôles
                Set<String> roles = user.getRoles().stream()
                        .map(role -> role.getName()) // adapte selon ta classe Role
                        .collect(Collectors.toSet());
                dto.setRoles(roles);
                // Ne JAMAIS renvoyer le mot de passe !
                dto.setPassword(null);
                return dto;
            }
        }
        return null;
    }


    // Récupérer tous les utilisateurs
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEnabled(user.isEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastLogin(user.getLastLogin());
        dto.setRoles(user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet()));

        return dto;
    }

    // récupérer un utilisateur par son id
    public Optional<UserDto> getUserById(String id) { // Optional permet à une valeure de pouvoir être null
        return userRepository.findById(id)
                .map(this::convertToDto);
    }

    // récupérer un utilisateur par son nom d'utilisateur
    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDto);
    }

    // récupérer un utilisateur par son email
    public UserDto createUser(UserDto userDto) {
        User user = convertToEntity(userDto);
        // Ajouter le rôle USER par défaut
        Set<Role> defaultRoles = roleService.getDefaultRoles();
        user.setRoles(defaultRoles);
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    // récupérer un utilisateur par son email
    public Optional<UserDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToDto);
    }

    private User convertToEntity(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEnabled(dto.isEnabled());

        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        // Note: Roles would be set separately by a RoleService

        return user;
    }

    // Mettre à jour un utilisateur
    public Optional<UserDto> updateUser(String id, UserDto userDto) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    // Met tout à jour sauf le password
                    existingUser.setFirstName(userDto.getFirstName());
                    existingUser.setLastName(userDto.getLastName());
                    existingUser.setEmail(userDto.getEmail());
                    existingUser.setEnabled(userDto.isEnabled());

                    User savedUser = userRepository.save(existingUser);
                    return convertToDto(savedUser);
                });
    }

    // Mettre à jour le mot de passe d'un utilisateur
    public boolean deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }



}
