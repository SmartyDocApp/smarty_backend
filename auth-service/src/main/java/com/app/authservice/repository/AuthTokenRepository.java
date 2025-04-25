package com.app.authservice.repository;

import com.app.authservice.model.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, String> {
    Optional<AuthToken> findByTokenValue(String tokenValue);
    List<AuthToken> findByUserIdAndTokenTypeAndRevoked(String userId, String tokenType, boolean revoked);
    void deleteByExpiryDateBefore(LocalDateTime date);
}
