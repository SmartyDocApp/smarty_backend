package com.app.authservice.dto.oauth2;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuth2UserInfo {
    private String id;
    private String name;
    private String email;
    private String imageUrl;
    private String provider;

    // Méthode utilitaire pour générer un nom d'utilisateur
    public String generateUsername() {
        return name.replaceAll("\\s+", "").toLowerCase() + "_" + id.substring(0, 5);
    }
}