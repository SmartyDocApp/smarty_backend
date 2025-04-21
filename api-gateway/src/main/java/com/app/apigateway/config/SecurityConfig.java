package com.app.apigateway.config;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Configuration
public class SecurityConfig {

    // JWT secret key for signing and verifying tokens
    @Value("${jwt.secret:defaultsecretkeymustbelongerthan256bits123456789101112}")
    private String jwtSecret;


    @Bean
    public JwtParser jwtParser() {
        // Create a JWT parser using the secret key ( parser is used to analyze and validate the token)
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        return Jwts.parserBuilder().setSigningKey(key).build();
    }

    @Bean
    public WebFilter jwtAuthenticationFilter() {
        // This filter intercepts incoming requests and checks for JWT tokens
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().value();

            // Skip authentication for public endpoints
            if (path.startsWith("/actuator") ||
                    path.startsWith("/fallback") ||
                    path.startsWith("/api/auth/login") ||
                    path.startsWith("/api/auth/register")) {
                return chain.filter(exchange);
            }

            // Process JWT token for protected endpoints
            return extractAndValidateToken(exchange)
                    .flatMap(isValid -> {
                        if (isValid) {
                            return chain.filter(exchange);
                        } else {
                            return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
                        }
                    })
                    .onErrorResume(error -> onError(exchange, error.getMessage(), HttpStatus.UNAUTHORIZED));
        };
    }

    // Extracts the JWT token from the Authorization header and validates it
    private Mono<Boolean> extractAndValidateToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.just(false);
        }

        String token = authHeader.substring(7);

        try {
            // Validation simple du token
            jwtParser().parseClaimsJws(token);
            return Mono.just(true);
        } catch (Exception e) {
            return Mono.just(false);
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }
}
