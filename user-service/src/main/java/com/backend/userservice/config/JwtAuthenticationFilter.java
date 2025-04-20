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

        // Si aucun token n'est fourni, renvoyer directement 401
        if (header == null || !header.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.getWriter().write("Authentication required");
            return; // Arrêter le traitement ici
        }

        // Traitement du token comme avant
        String token = header.substring(7);

        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();

            String username = claims.getSubject();
            List<String> authorities = (List<String>) claims.get("authorities");

            if (username != null) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.getWriter().write("Invalid token");
            return; // Arrêter le traitement ici
        }

        filterChain.doFilter(request, response);
    }

}

