package com.backend.userservice.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtParser jwtParser;


    public JwtAuthenticationFilter(JwtParser jwtParser) {
        this.jwtParser = jwtParser;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Vérifie si l'en-tête "Authorization" est absent ou ne commence pas par "Bearer ".
        // Si c'est le cas, la requête est transmise au filtre suivant sans traitement supplémentaire.
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Récupère le token JWT à partir de l'en-tête "Authorization" en supprimant le préfixe "Bearer ".
        String token = header.substring(7);

        // Vérifie si le token est vide ou nul.
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();

            String username = claims.getSubject();
            List<String> authorities = (List<String>) claims.get("authorities");

            // Si le nom d'utilisateur est présent, crée un objet UsernamePasswordAuthenticationToken
            if (username != null) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (JwtException e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

}

