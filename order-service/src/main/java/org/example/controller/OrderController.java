package org.example.controller;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.model.Order;
import com.example.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "orderService", fallbackMethod = "createOrderFallback")
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }
    
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }
    
    @GetMapping("/number/{orderNumber}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrderByOrderNumber(@PathVariable String orderNumber) {
        return orderService.getOrderByOrderNumber(orderNumber);
    }
    
    @GetMapping("/customer/{email}")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrdersByCustomerEmail(@PathVariable String email) {
        return orderService.getOrdersByCustomerEmail(email);
    }
    
    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse updateOrderStatus(@PathVariable Long id, 
                                          @RequestParam Order.OrderStatus status) {
        return orderService.updateOrderStatus(id, status);
    }
    
    // Fallback method per Circuit Breaker
    public OrderResponse createOrderFallback(OrderRequest orderRequest, Throwable e) {
        // In un'implementazione reale, si potrebbe salvare l'ordine in uno stato di "pending"
        // o implementare un sistema di coda per ritentare più tardi
        return OrderResponse.builder()
                .orderNumber("FALLBACK-" + System.currentTimeMillis())
                .status(Order.OrderStatus.CANCELLED)
                .build();
    }
    
    // Endpoint di test per simulare problemi (utile per testare Circuit Breaker)
    @GetMapping("/test-error")
    public ResponseEntity<Object> testError(@RequestParam(defaultValue = "false") boolean throwError, 
                                           @RequestParam(defaultValue = "0") long delayMs) {
        if (throwError) {
            throw new RuntimeException("Test error for circuit breaker");
        }
        
        if (delayMs > 0) {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Test endpoint working normally");
        response.put("delay", delayMs);
        
        return ResponseEntity.ok(response);
    }
}