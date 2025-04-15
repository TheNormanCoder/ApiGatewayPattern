package org.example.client;

import org.example.dto.ProductResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductClient {
    
    @GetMapping("/products/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "getDefaultProduct")
    ProductResponse getProductById(@PathVariable("id") Long id);
    
    default ProductResponse getDefaultProduct(Long id, Throwable e) {
        return ProductResponse.builder()
                .id(id)
                .name("Fallback Product")
                .description("This is a fallback product when service is down")
                .price(java.math.BigDecimal.ZERO)
                .stock(0)
                .category("Fallback")
                .build();
    }
}