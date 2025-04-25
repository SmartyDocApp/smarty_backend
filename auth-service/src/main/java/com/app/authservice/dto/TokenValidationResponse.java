package com.app.authservice.dto;

import java.util.List;

/**
 * TokenValidationResponse is a DTO that represents the response of a token validation request.
 * It contains information about the validity of the token, the username associated with it,
 * and the list of authorities granted to the user.
 */
public class TokenValidationResponse {
    private boolean valid;
    private String username;
    private List<String> authorities;

    // Getters and Setters
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}