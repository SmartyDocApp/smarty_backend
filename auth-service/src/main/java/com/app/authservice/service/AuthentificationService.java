package com.app.authservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.authservice.client.UserServiceClient;
import com.app.authservice.dto.LoginRequest;
import com.app.authservice.dto.RegisterRequest;
import com.app.authservice.dto.TokenResponse;
import com.app.authservice.dto.TokenValidationResponse;
import com.app.authservice.dto.UserDto;
import com.app.authservice.exception.DuplicateUserException;
import com.app.authservice.exception.InvalidTokenException;
import com.app.authservice.model.AuthToken;
import com.app.authservice.repository.AuthTokenRepository;

@Service
public class AuthentificationService {
    private final JwtService jwtService;
    private final UserServiceClient userServiceClient;
    private final AuthTokenRepository tokenRepository;
    
    @Autowired
    public AuthentificationService(JwtService jwtService, 
                               UserServiceClient userServiceClient,
                               AuthTokenRepository tokenRepository) {
        this.jwtService = jwtService;
        this.userServiceClient = userServiceClient;
        this.tokenRepository = tokenRepository;
    }
    
    public TokenResponse login(LoginRequest loginRequest) {
        // Appel au user-service pour vérification
        ResponseEntity<UserDto> userResponse = userServiceClient.getUserByUsername(loginRequest.getUsername());
        
        if (userResponse.getStatusCode() != HttpStatus.OK) {
            throw new UsernameNotFoundException("Utilisateur non trouvé");
        }
        
        UserDto user = userResponse.getBody();
        
        // En production, la vérification du mot de passe devrait être déléguée au user-service
        // Ici simplifié pour la démonstration
        
        // Génération des tokens
        List<String> roles = new ArrayList<>(user.getRoles());
        String accessToken = jwtService.generateAccessToken(user.getUsername(), roles);
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        
        // Stockage des tokens
        saveToken(user.getId(), accessToken, "ACCESS");
        saveToken(user.getId(), refreshToken, "REFRESH");
        
        // Mise à jour de la dernière connexion
        Map<String, LocalDateTime> lastLoginMap = new HashMap<>();
        lastLoginMap.put("lastLogin", LocalDateTime.now());
        userServiceClient.updateLastLogin(user.getId(), lastLoginMap);
        
        // Construction de la réponse
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        tokenResponse.setExpiresAt(LocalDateTime.now().plusSeconds(86400)); // 24h
        
        return tokenResponse;
    }
    
    public TokenValidationResponse validateToken(String token) {
        TokenValidationResponse response = new TokenValidationResponse();
        
        if (!jwtService.validateToken(token)) {
            response.setValid(false);
            return response;
        }
        
        // Vérification en base si le token n'est pas révoqué
        Optional<AuthToken> storedToken = tokenRepository.findByTokenValue(token);
        if (storedToken.isEmpty() || storedToken.get().isRevoked()) {
            response.setValid(false);
            return response;
        }
        
        String username = jwtService.extractUsername(token);
        List<String> authorities = jwtService.extractAuthorities(token);
        
        response.setValid(true);
        response.setUsername(username);
        response.setAuthorities(authorities);
        
        return response;
    }
    
    public TokenResponse refreshToken(String refreshToken) {
        // Validation du refresh token
        if (!jwtService.validateToken(refreshToken)) {
            throw new InvalidTokenException("Refresh token invalide");
        }
        
        String username = jwtService.extractUsername(refreshToken);
        
        // Appel au user-service pour récupérer les informations à jour
        ResponseEntity<UserDto> userResponse = userServiceClient.getUserByUsername(username);
        
        if (userResponse.getStatusCode() != HttpStatus.OK) {
            throw new UsernameNotFoundException("Utilisateur non trouvé");
        }
        
        UserDto user = userResponse.getBody();
        
        // Révocation des anciens tokens d'accès
        revokeAccessTokens(user.getId());
        
        // Génération d'un nouveau token d'accès
        List<String> roles = new ArrayList<>(user.getRoles());
        String newAccessToken = jwtService.generateAccessToken(username, roles);
        
        // Stockage du nouveau token
        saveToken(user.getId(), newAccessToken, "ACCESS");
        
        // Construction de la réponse
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(newAccessToken);
        tokenResponse.setRefreshToken(refreshToken); // On garde le même refresh token
        tokenResponse.setExpiresAt(LocalDateTime.now().plusSeconds(86400)); // 24h
        
        return tokenResponse;
    }
    
    public void revokeToken(String token) {
        Optional<AuthToken> storedToken = tokenRepository.findByTokenValue(token);
        storedToken.ifPresent(authToken -> {
            authToken.setRevoked(true);
            tokenRepository.save(authToken);
        });
    }
    
    private void saveToken(String userId, String tokenValue, String tokenType) {
        AuthToken token = new AuthToken();
        token.setUserId(userId);
        token.setTokenValue(tokenValue);
        token.setTokenType(tokenType);
        token.setRevoked(false);
        
        LocalDateTime expiryDate = "ACCESS".equals(tokenType) 
            ? LocalDateTime.now().plusSeconds(86400) // 24h
            : LocalDateTime.now().plusSeconds(604800); // 7 jours
            
        token.setExpiryDate(expiryDate);
        
        tokenRepository.save(token);
    }
    
    public TokenResponse register(RegisterRequest request) {
        ResponseEntity<UserDto> userResponse = userServiceClient.createUser(request);

        if (userResponse.getStatusCode() != HttpStatus.OK && userResponse.getStatusCode() != HttpStatus.CREATED) {
            // Log du code et du message d'erreur du user-service
            String errorMsg = "Erreur user-service: code=" + userResponse.getStatusCode() + ", body=" + userResponse.getBody();
            System.err.println(errorMsg);

            // Gestion du cas de doublon
            if (userResponse.getStatusCode() == HttpStatus.CONFLICT) {
                throw new DuplicateUserException("Un utilisateur avec ce nom ou cet email existe déjà.");
            }

            throw new RuntimeException("Échec de l'enregistrement de l'utilisateur");
        }

        UserDto user = userResponse.getBody();

        // Génération des tokens
        List<String> roles = new ArrayList<>(user.getRoles());
        String accessToken = jwtService.generateAccessToken(user.getUsername(), roles);
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        // Stockage des tokens
        saveToken(user.getId(), accessToken, "ACCESS");
        saveToken(user.getId(), refreshToken, "REFRESH");

        // Construction de la réponse
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        tokenResponse.setExpiresAt(LocalDateTime.now().plusSeconds(86400)); // 24h

        return tokenResponse;
    }
    
    private void revokeAccessTokens(String userId) {
        List<AuthToken> activeTokens = tokenRepository.findByUserIdAndTokenTypeAndRevoked(
            userId, "ACCESS", false);
            
        activeTokens.forEach(token -> {
            token.setRevoked(true);
            tokenRepository.save(token);
        });
    }
}