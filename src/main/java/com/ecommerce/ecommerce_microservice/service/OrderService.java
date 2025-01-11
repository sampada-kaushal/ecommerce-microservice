package com.ecommerce.ecommerce_microservice.service;

import com.ecommerce.ecommerce_microservice.dto.requestDto.OrderRequestDto;
import com.ecommerce.ecommerce_microservice.dto.requestDto.OrderStatusRequestDto;
import com.ecommerce.ecommerce_microservice.dto.responseDto.OrderResponseDto;
import com.ecommerce.ecommerce_microservice.dto.responseDto.OrderStatusResponseDto;
import com.ecommerce.ecommerce_microservice.entity.*;
import com.ecommerce.ecommerce_microservice.exception.InsufficientInventoryException;
import com.ecommerce.ecommerce_microservice.exception.InvalidOrderStatusException;
import com.ecommerce.ecommerce_microservice.exception.OrderNotFoundException;
import com.ecommerce.ecommerce_microservice.exception.ProductNotFoundException;
import com.ecommerce.ecommerce_microservice.repository.*;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto request) {
        // Logic for placing an order
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderRequestDto.ProductItem item : request.getItems()) {
            Product product = productRepository.findById((long) item.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found: " + item.getProductId()));
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientInventoryException("Not enough stock for product: " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            orderItems.add(orderItem);

            totalPrice = totalPrice.add(orderItem.getPrice());
        }
        Orders order = new Orders();
        order.setUserId((long) request.getUserId());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(totalPrice);

        Orders savedOrder = orderRepository.save(order);
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(savedOrder);
            orderItemRepository.save(orderItem);
        }
        return new OrderResponseDto(savedOrder.getId().intValue(), savedOrder.getStatus().name(),
                totalPrice.doubleValue());
    }

    @Transactional
    public OrderStatusResponseDto updateOrderStatus(Long orderId, OrderStatusRequestDto request) {
        // Logic for updating order status
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found : " + orderId));
        String inputStatus = request.getStatus().toUpperCase();

        // Validate the status
        if (!isValidOrderStatus(inputStatus)) {
            throw new InvalidOrderStatusException("Invalid order status: " + inputStatus);
        }
        order.setStatus(OrderStatus.valueOf(inputStatus));
        Orders updatedOrder = orderRepository.save(order);
        return new OrderStatusResponseDto(updatedOrder.getId().intValue(), updatedOrder.getStatus().name());
    }

    // Helper method to validate the status
    private boolean isValidOrderStatus(String status) {
        try {
            OrderStatus.valueOf(status); // Will throw IllegalArgumentException if invalid
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
