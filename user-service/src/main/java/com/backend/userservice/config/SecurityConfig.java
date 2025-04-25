package com.backend.userservice.config;



import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

   // @Value permet d'injecter des valeurs provenant de fichiers de configuration
    // injecte la propriété jwt.secret
   @Value("${jwt.secret:defaultsecretkeymustbelongerthan256bits123456789101112}")
   private String jwtSecret;

   @Autowired
   private UserDetailsService userDetailsService;

   @Bean
   public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
   }

   @Bean
   public AuthenticationManager authenticationManager() {
       DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       authProvider.setUserDetailsService(userDetailsService);
       authProvider.setPasswordEncoder(passwordEncoder());
       return new ProviderManager(authProvider);
   }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtParser());
    }

    // Configurer la sécurité HTTP
    @Bean
    public JwtParser jwtParser() {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        return Jwts.parserBuilder().setSigningKey(key).build();
    }

    // La méthode securityFilterChain configure la chaîne de filtres de sécurité
    // exige une authentification pour toutes les requêtes sauf celles correspondant à /actuator/**.
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
              .csrf(csrf -> csrf.disable())
              .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .authorizeHttpRequests(auth -> auth
                      .requestMatchers("/actuator/**").permitAll() // Pour le monitoring
                      .requestMatchers("/api/auth/**").permitAll() // Authentication endpoints
                      .requestMatchers("/api/public/**").permitAll() // Public endpoints
                      .anyRequest().authenticated()
              )
              .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

      return http.build();
  }



}
