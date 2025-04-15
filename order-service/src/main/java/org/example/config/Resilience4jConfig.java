package org.example.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {
        // Default circuit breaker configuration
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)               // Percentuale di fallimenti che fa aprire il circuito
                .waitDurationInOpenState(Duration.ofMillis(5000))  // Tempo di attesa prima di passare a HALF_OPEN
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)                   // Numero di chiamate considerate per calcolare il failure rate
                .permittedNumberOfCallsInHalfOpenState(3) // Numero di chiamate permesse in stato HALF_OPEN
                .minimumNumberOfCalls(5)                 // Numero minimo di chiamate prima di calcolare il failure rate
                .build();

        // Default time limiter configuration (timeout)
        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(3))  // Timeout dopo il quale una chiamata è considerata fallita
                .build();

        // Return factory customizer
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(timeLimiterConfig)
                .circuitBreakerConfig(circuitBreakerConfig)
                .build());
    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> productServiceCustomizer() {
        // Configurazione specifica per il servizio product
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(30)                // Threshold più basso per product service
                .waitDurationInOpenState(Duration.ofMillis(3000))
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(5)
                .permittedNumberOfCallsInHalfOpenState(2)
                .minimumNumberOfCalls(3)
                .build();

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(2))  // Timeout più breve per product service
                .build();

        return factory -> factory.configure(builder -> builder
                        .circuitBreakerConfig(circuitBreakerConfig)
                        .timeLimiterConfig(timeLimiterConfig),
                "productService"); // Nome del circuit breaker
    }
}