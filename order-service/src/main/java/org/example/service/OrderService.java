package org.example.service;

import com.example.orderservice.client.ProductClient;
import com.example.orderservice.dto.*;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderItem;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.OrderItemResponse;
import org.example.dto.OrderRequest;
import org.example.dto.OrderResponse;
import org.example.dto.ProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        // Genera un numero ordine univoco
        String orderNumber = UUID.randomUUID().toString();

        // Crea un nuovo ordine
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .orderDate(LocalDateTime.now())
                .customerName(orderRequest.getCustomerName())
                .customerEmail(orderRequest.getCustomerEmail())
                .shippingAddress(orderRequest.getShippingAddress())
                .status(Order.OrderStatus.CREATED)
                .build();

        // Crea gli item dell'ordine e calcola il totale
        List<OrderItem> orderItems = orderRequest.getOrderItems().stream()
                .map(itemRequest -> {
                    // Ottiene i dettagli del prodotto tramite Feign Client (con circuit breaker)
                    ProductResponse product = productClient.getProductById(itemRequest.getProductId());

                    // Calcola il subtotale
                    BigDecimal subtotal = product.getPrice()
                            .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

                    // Crea l'item dell'ordine
                    return OrderItem.builder()
                            .productId(product.getId())
                            .productName(product.getName())
                            .price(product.getPrice())
                            .quantity(itemRequest.getQuantity())
                            .subtotal(subtotal)
                            .order(order)
                            .build();
                })
                .collect(Collectors.toList());

        // Calcola il totale dell'ordine
        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        // Salva l'ordine nel database
        Order savedOrder = orderRepository.save(order);
        log.info("Order {} is created", savedOrder.getId());

        // Converte l'ordine in risposta
        return mapToOrderResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        return mapToOrderResponse(order);
    }

    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with order number: " + orderNumber));

        return mapToOrderResponse(order);
    }

    public List<OrderResponse> getOrdersByCustomerEmail(String customerEmail) {
        List<Order> orders = orderRepository.findByCustomerEmail(customerEmail);

        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} status updated to {}", updatedOrder.getId(), status);

        return mapToOrderResponse(updatedOrder);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderDate(order.getOrderDate())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .shippingAddress(order.getShippingAddress())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .orderItems(orderItemResponses)
                .build();
    }
}