package com.app.apigateway.config;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;

@Configuration
@EnableWebFluxSecurity
public class OAuth2SecurityConfig {

    @Value("${security.oauth2.client.registration.auth-service.client-id}")
    private String clientId;
    
    @Value("${security.oauth2.client.registration.auth-service.client-secret}")
    private String clientSecret;
    
    @Value("${security.oauth2.client.registration.auth-service.redirect-uri}")
    private String redirectUri;
    
    @Value("${security.oauth2.client.provider.auth-service.authorization-uri}")
    private String authorizationUri;
    
    @Value("${security.oauth2.client.provider.auth-service.token-uri}")
    private String tokenUri;
    
    @Value("${security.oauth2.client.provider.auth-service.user-info-uri}")
    private String userInfoUri;
    
    @Value("${security.oauth2.client.provider.auth-service.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration authServiceRegistration = ClientRegistration.withRegistrationId("auth-service")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(redirectUri)
                .scope("openid", "read", "write")
                .authorizationUri(authorizationUri)
                .tokenUri(tokenUri)
                .userInfoUri(userInfoUri)
                .jwkSetUri(jwkSetUri)
                .userNameAttributeName("sub")
                .build();
        
        return new InMemoryReactiveClientRegistrationRepository(authServiceRegistration);
    }

    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // Configuration des redirections après connexion et déconnexion
        RedirectServerAuthenticationSuccessHandler successHandler = new RedirectServerAuthenticationSuccessHandler();
        successHandler.setLocation(URI.create("/"));
        
        RedirectServerLogoutSuccessHandler logoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        logoutSuccessHandler.setLogoutSuccessUrl(URI.create("/login?logout"));
        
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/fallback/**").permitAll()
                        .pathMatchers("/auth/**", "/api/auth/**", "/login/**", "/oauth2/**").permitAll()
                        .pathMatchers("/api/users/register").permitAll()
                        // Pour les tests seulement, à supprimer en production
                        .pathMatchers("/api/users").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(successHandler)
                        .authorizedClientRepository(authorizedClientRepository())
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(logoutSuccessHandler)
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .build();
    }

    @Bean
    public WebSessionManager webSessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
        resolver.setCookieName("GATEWAY_SESSION");
        resolver.setCookieMaxAge(java.time.Duration.ofHours(1));
        // Ces méthodes ne sont pas disponibles dans cette version de Spring
        // resolver.setCookiePath("/");
        // resolver.setCookieHttpOnly(true);
        // En production, activez le secure flag si disponible
        // resolver.setCookieSecure(true);
        sessionManager.setSessionIdResolver(resolver);
        return sessionManager;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}