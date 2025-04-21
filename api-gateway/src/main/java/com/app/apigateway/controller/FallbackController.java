package com.app.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user-service")
    public Mono<ResponseEntity<Object>> userServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Le service utilisateur est indisponible. Veuillez réessayer plus tard.");

        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }

    @GetMapping("/document-service")
    public Mono<ResponseEntity<Object>> documentServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Le service document est indisponible. Veuillez réessayer plus tard.");

        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }

    @GetMapping("/workspace-service")
    public Mono<ResponseEntity<Object>> workspaceServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Le service workspace est indisponible. Veuillez réessayer plus tard.");

        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }

    @GetMapping("/chat-service")
    public Mono<ResponseEntity<Object>> chatServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Le service chat est indisponible. Veuillez réessayer plus tard.");

        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }

    @GetMapping("/auth-service")
    public Mono<ResponseEntity<Object>> authServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Le service d'authentification est indisponible. Veuillez réessayer plus tard.");

        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
}
