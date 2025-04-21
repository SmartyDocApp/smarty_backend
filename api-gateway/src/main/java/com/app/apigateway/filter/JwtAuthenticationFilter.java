package com.app.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JwtAuthenticationFilter is a Spring Cloud Gateway filter that handles JWT authentication.
 * It checks for the presence of a JWT token in the Authorization header and validates it.
 * If the token is valid, it extracts user information and adds it to the request headers.
 */

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtParser jwtParser;

    public JwtAuthenticationFilter(JwtParser jwtParser) {
        super(Config.class);
        this.jwtParser = jwtParser;
    }

    @Override
    public GatewayFilter apply(Config config) {
        // This method is called to apply the filter to the request
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Vérifier si le chemin est exempté d'authentification
            if (config.isApiSecured()) {
                if (!request.getHeaders().containsKey("Authorization")) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                String token = request.getHeaders().getOrEmpty("Authorization").get(0);
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                } else {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                try {
                    // Valider le token JWT
                    Claims claims = jwtParser.parseClaimsJws(token).getBody();
                    String username = claims.getSubject();
                    List<String> roles = (List<String>) claims.get("roles");

                    // Ajouter les informations d'utilisateur aux headers pour les services en aval
                    exchange.getRequest().mutate()
                            .header("X-User-Username", username)
                            .header("X-User-Roles", String.join(",", roles))
                            .build();

                } catch (Exception e) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {
        private boolean apiSecured = true;

        public boolean isApiSecured() {
            return apiSecured;
        }

        public void setApiSecured(boolean apiSecured) {
            this.apiSecured = apiSecured;
        }
    }
}