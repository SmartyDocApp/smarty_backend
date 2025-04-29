package com.app.authservice.client;

import java.time.LocalDateTime;
import java.util.Map;

import com.app.authservice.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.app.authservice.dto.UserDto;

@Component
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public ResponseEntity<UserDto> getUserByUsername(String username) {
        return ResponseEntity.notFound().build();
    }

//    @Override
//    public ResponseEntity<Void> updateLastLogin(String id, Map<String, LocalDateTime> lastLoginMap) {
//        return ResponseEntity.ok().build();
//    }

    @Override
    public ResponseEntity<UserDto> createUser(RegisterRequest registerRequest) {
        return ResponseEntity.status(503).build(); // Service indisponible
    }

    @Override
    public ResponseEntity<UserDto> loginUser(Map<String, String> loginRequest) {
        // Ici, tu peux retourner une réponse d’erreur ou null selon ta logique
        return ResponseEntity.status(503).build(); // 503 Service Unavailable
    }

    @Override
    public ResponseEntity<UserDto> getUserByEmail(String email) {
        return ResponseEntity.notFound().build();
    }
} 