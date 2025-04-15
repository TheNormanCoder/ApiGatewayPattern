package org.example;

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

    @GetMapping("/products")
    public Mono<ResponseEntity<Map<String, String>>> productServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Product Service is currently unavailable. Please try again later.");
        
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }

    @GetMapping("/orders")
    public Mono<ResponseEntity<Map<String, String>>> orderServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Order Service is currently unavailable. Please try again later.");
        
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
}