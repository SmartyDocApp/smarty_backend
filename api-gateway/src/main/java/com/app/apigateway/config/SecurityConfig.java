package com.app.apigateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.function.Predicate;

@Configuration
public class SecurityConfig {

    // JWT secret key for signing and verifying tokens
    // the value in the braces is the default value if the property is not set
    @Value("${jwt.secret}")
    private String jwtSecret;


    @Bean
    public JwtParser jwtParser() {
        // Create a JWT parser using the secret key ( parser is used to analyze and validate the token)
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        return Jwts.parserBuilder().setSigningKey(key).build();
    }

    @Bean
    public WebFilter jwtAuthWebFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().value();

            // Définir tous les chemins publics ici
            if (path.startsWith("/actuator") ||
                    path.startsWith("/fallback") ||
                    path.startsWith("/api/auth/login") ||
                    path.startsWith("/api/auth/register")) {
                return chain.filter(exchange);
            }

            // Vérifier le JWT pour les autres chemins
            return extractAndValidateToken(exchange)
                    .flatMap(claims -> {
                        if (claims != null) {
                            // Ajouter les infos utilisateur comme headers
                            ServerHttpRequest request = exchange.getRequest().mutate()
                                    .header("X-User-Id", claims.get("userId", String.class))
                                    .header("X-User-Name", claims.getSubject())
                                    .header("X-User-Roles", String.join(",",
                                            claims.get("authorities", List.class)))
                                    .build();

                            return chain.filter(exchange.mutate().request(request).build());
                        } else {
                            return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
                        }
                    })
                    .onErrorResume(error -> onError(exchange, error.getMessage(), HttpStatus.UNAUTHORIZED));
        };
    }

    // Extracts the JWT token from the Authorization header and validates it
    private Mono<Claims> extractAndValidateToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.empty();
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtParser().parseClaimsJws(token).getBody();
            return Mono.just(claims);
        } catch (Exception e) {
            return Mono.empty();
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }
}
