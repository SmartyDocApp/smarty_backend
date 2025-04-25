package com.app.apigateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory.RetryConfig;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customeRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                /**
                 * Routes for the different services
                 * Each route is configured with a circuit breaker and a rewrite path filter
                 * The circuit breaker will redirect to a fallback URI if the service is unavailable
                 * The rewrite path filter will modify the request path before forwarding it to the service
                 */

                // User Service Routes
                .route("user-service", r -> r.path("/api/users/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("userServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/user-service"))
                                .rewritePath("/api/users/(?<segment>.*)", "/api/users/${segment}"))
                        .uri("lb://user-service"))

//                // Document Service Routes (à implémenter ultérieurement)
                .route("document-service", r -> r.path("/api/documents/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("documentServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/document-service"))
                                .rewritePath("/api/documents/(?<segment>.*)", "/api/documents/${segment}"))
                        .uri("lb://document-service"))

                // Workspace Service Routes (à implémenter ultérieurement)
                .route("workspace-service", r -> r.path("/api/workspaces/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("workspaceServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/workspace-service"))
                                .rewritePath("/api/workspaces/(?<segment>.*)", "/api/workspaces/${segment}"))
                        .uri("lb://workspace-service"))

                // Chat Service Routes (à implémenter ultérieurement)
                .route("chat-service", r -> r.path("/api/chat/**", "/ws/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("chatServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/chat-service")))
                        .uri("lb://chat-service"))

                // Auth Service Routes (à implémenter ultérieurement)
                .route("auth-service", r -> r.path("/api/auth/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("authServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/auth-service"))
                                .rewritePath("/api/auth/(?<segment>.*)", "/api/auth/${segment}"))
                        .uri("lb://auth-service"))

                .build();
    }

    // Default parameters for Circuit breakers with Resilience4J
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .slidingWindowSize(10)
                .build();

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(5))
                .build();

        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(circuitBreakerConfig)
                .timeLimiterConfig(timeLimiterConfig)
                .build());
    }

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            if (exchange.getRequest().getRemoteAddress() != null &&
                    exchange.getRequest().getRemoteAddress().getAddress() != null) {
                return Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
            }
            // Fournir une valeur par défaut en cas d'adresse nulle
            return Mono.just("unknown-host");
        };
    }

}
