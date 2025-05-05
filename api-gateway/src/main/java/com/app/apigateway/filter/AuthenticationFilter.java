package com.app.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Filtre global pour propager les informations d'authentification aux services en aval.
 * Ce filtre extrait les informations de l'utilisateur connecté et les ajoute en tant qu'en-têtes
 * à la requête afin que les microservices puissent identifier l'utilisateur sans avoir à
 * ré-authentifier la requête.
 */
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(authentication -> authentication != null && authentication.isAuthenticated())
                .map(authentication -> withAuthenticationHeaders(exchange, authentication))
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    private ServerWebExchange withAuthenticationHeaders(ServerWebExchange exchange, Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Auth = (OAuth2AuthenticationToken) authentication;
            OAuth2User principal = oauth2Auth.getPrincipal();
            
            logger.debug("Propagating authentication for user: {}", principal.getName());
            
            // Construire une requête avec les en-têtes d'authentification
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("X-User-Id", principal.getName())
                    .header("X-User-Name", principal.getAttribute("preferred_username") != null ? 
                            principal.getAttribute("preferred_username") : principal.getName())
                    .header("X-User-Roles", String.join(",", oauth2Auth.getAuthorities().stream()
                            .map(authority -> authority.getAuthority())
                            .toArray(String[]::new)))
                    .build();
            
            // Retourner un nouvel échange avec la requête modifiée
            return exchange.mutate().request(request).build();
        }
        
        return exchange;
    }

    @Override
    public int getOrder() {
        // Exécuter ce filtre après l'authentification mais avant d'autres filtres
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
} 