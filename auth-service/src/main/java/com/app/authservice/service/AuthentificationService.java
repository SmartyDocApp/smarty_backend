package com.app.authservice.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.authservice.client.UserServiceClient;
import com.app.authservice.dto.CustomTokenResponse;
import com.app.authservice.dto.LoginRequest;
import com.app.authservice.dto.TokenValidationResponse;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthentificationService {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession httpSession;
    private final UserServiceClient userServiceClient;

    public CustomTokenResponse login(LoginRequest loginRequest) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Stocker les informations de session
        httpSession.setAttribute("USER_USERNAME", userDetails.getUsername());
        httpSession.setAttribute("USER_AUTHORITIES", userDetails.getAuthorities());
        
        // Créer une réponse "compatible" avec l'ancienne implémentation JWT
        return CustomTokenResponse.builder()
                .accessToken("session-auth")
                .refreshToken("session-refresh")
                .tokenType("Session")
                .expiresIn(1800L) // 30 minutes
                .build();
    }
    
    public CustomTokenResponse refreshToken(String refreshToken) {
        // Vérifier si la session est toujours valide
        String username = (String) httpSession.getAttribute("USER_USERNAME");
        if (username == null) {
            throw new RuntimeException("Session invalide ou expirée");
        }
        
        // Prolonger la session
        httpSession.setMaxInactiveInterval(1800); // 30 minutes
        
        return CustomTokenResponse.builder()
                .accessToken("session-auth-refreshed")
                .refreshToken("session-refresh")
                .tokenType("Session")
                .expiresIn(1800L)
                .build();
    }
    
    public TokenValidationResponse validateToken(String token) {
        // Vérifier si la session est valide
        String username = (String) httpSession.getAttribute("USER_USERNAME");
        boolean isValid = username != null;
        
        // Vérifier l'utilisateur via le service utilisateur avec Feign
        if (isValid && username != null) {
            try {
                var response = userServiceClient.existsByUsername(username);
                isValid = response.getBody() != null && response.getBody();
            } catch (Exception e) {
                isValid = false;
            }
        }
        
        return TokenValidationResponse.builder()
                .valid(isValid)
                .username(username)
                .error(isValid ? null : "Session invalide ou utilisateur non trouvé")
                .build();
    }
    
    public void revokeToken(String token) {
        // Invalider la session
        httpSession.invalidate();
    }
} 