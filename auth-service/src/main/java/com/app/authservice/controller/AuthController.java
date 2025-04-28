package com.app.authservice.controller;

import com.app.authservice.dto.*;
import com.app.authservice.service.AuthentificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private final AuthentificationService authentificationService;

    public AuthController(AuthentificationService authentificationService) {
        this.authentificationService = authentificationService;
    }

    // Endpoint for login
    @PostMapping("/login")
    public ResponseEntity <TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        TokenResponse response = authentificationService.login(loginRequest);
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
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody TokenValidationRequest refreshToken){
        TokenResponse tokenResponse = authentificationService.refreshToken(refreshToken.getToken());
        return ResponseEntity.ok(tokenResponse);
    }

    // Endpoint for logout
    @PostMapping("/logout")
    public ResponseEntity<Void> Logout (@RequestBody TokenValidationRequest request){
        authentificationService.revokeToken(request.getToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest registerRequest) {
        TokenResponse response = authentificationService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    




}

