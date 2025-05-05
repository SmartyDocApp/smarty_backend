package com.app.authservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.authservice.dto.CustomTokenResponse;
import com.app.authservice.dto.LoginRequest;
import com.app.authservice.dto.TokenValidationRequest;
import com.app.authservice.dto.TokenValidationResponse;
import com.app.authservice.dto.UserRegistrationDto;
import com.app.authservice.model.User;
import com.app.authservice.service.AuthentificationService;
import com.app.authservice.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthentificationService authentificationService;
    private final UserService userService;

    // Endpoint for login
    @PostMapping("/login")
    public ResponseEntity<CustomTokenResponse> login(@RequestBody LoginRequest loginRequest) {
        CustomTokenResponse response = authentificationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    //token validation endpoint
    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody String token){
        TokenValidationResponse response = authentificationService.validateToken(token);
        return ResponseEntity.ok(response);
    }

    // Endpoint for refresh token
    @PostMapping("/refresh")
    public ResponseEntity<CustomTokenResponse> refreshToken(@RequestBody TokenValidationRequest refreshToken){
        CustomTokenResponse tokenResponse = authentificationService.refreshToken(refreshToken.getToken());
        return ResponseEntity.ok(tokenResponse);
    }

    // Endpoint for logout
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody TokenValidationRequest request){
        authentificationService.revokeToken(request.getToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            User user = userService.registerUser(registrationDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("message", "Utilisateur enregistré avec succès");
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}

