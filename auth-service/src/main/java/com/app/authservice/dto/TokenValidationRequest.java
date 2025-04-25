package com.app.authservice.dto;

/**
 * TokenValidationRequest is a DTO for validating tokens.
 * It contains the token to be validated.
 */
public class TokenValidationRequest {
    private String token;

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
