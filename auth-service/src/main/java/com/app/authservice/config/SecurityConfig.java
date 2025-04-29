package com.app.authservice.config;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        // Faire confiance aux requêtes venant de l'API Gateway
                        .anyRequest().permitAll()
                );

        // Supprimer le filtre JWT, plus nécessaire ici
        // .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //  filtre qui extrait l'utilisateur des headers
    @Bean
    public OncePerRequestFilter userContextFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                    throws ServletException, IOException, java.io.IOException {

                // Extraire les infos utilisateur des headers
                String userId = request.getHeader("X-User-Id");
                String username = request.getHeader("X-User-Name");
                String roles = request.getHeader("X-User-Roles");

                // Si les headers sont présents, créer un context d'authentification
                if (username != null) {
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    if (roles != null) {
                        for (String role : roles.split(",")) {
                            authorities.add(new SimpleGrantedAuthority(role));
                        }
                    }

                    // Créer un token d'authentification
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            username, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }

                chain.doFilter(request, response);
            }
        };
    }
}