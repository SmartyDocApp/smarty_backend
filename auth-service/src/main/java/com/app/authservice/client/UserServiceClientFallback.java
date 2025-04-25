package com.app.authservice.client;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.app.authservice.dto.UserDto;

@Component
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public ResponseEntity<UserDto> getUserByUsername(String username) {
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> updateLastLogin(String id, Map<String, LocalDateTime> lastLoginMap) {
        return ResponseEntity.ok().build();
    }
} 