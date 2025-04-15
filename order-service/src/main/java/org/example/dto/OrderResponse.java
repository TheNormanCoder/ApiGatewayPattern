package org.example.dto;

import com.example.orderservice.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private LocalDateTime orderDate;
    private String customerName;
    private String customerEmail;
    private String shippingAddress;
    private BigDecimal totalAmount;
    private Order.OrderStatus status;
    private List<OrderItemResponse> orderItems;
}