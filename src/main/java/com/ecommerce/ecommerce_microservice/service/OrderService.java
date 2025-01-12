package com.ecommerce.ecommerce_microservice.service;

import com.ecommerce.ecommerce_microservice.dto.requestDto.OrderRequestDto;
import com.ecommerce.ecommerce_microservice.dto.requestDto.OrderStatusRequestDto;
import com.ecommerce.ecommerce_microservice.dto.responseDto.OrderResponseDto;
import com.ecommerce.ecommerce_microservice.dto.responseDto.OrderStatusResponseDto;
import com.ecommerce.ecommerce_microservice.entity.*;
import com.ecommerce.ecommerce_microservice.exception.InsufficientInventoryException;
import com.ecommerce.ecommerce_microservice.exception.InvalidOrderStatusException;
import com.ecommerce.ecommerce_microservice.exception.OrderNotFoundException;
import com.ecommerce.ecommerce_microservice.exception.RateLimitExceededException;
import com.ecommerce.ecommerce_microservice.repository.*;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.resilience4j.ratelimiter.RateLimiter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private InventoryService inventoryService;
    private final OrderMessageProducer orderMessageProducer;
    @Autowired
    private RateLimiter rateLimiter;
    
    @Autowired
    public OrderService(OrderMessageProducer orderMessageProducer) {
       
        this.orderMessageProducer = orderMessageProducer;
    }

    //Added a new function to test Queue implementation via unit test, instead of updating current implementation
    public OrderResponseDto placeOrderQueue(OrderRequestDto request) {
        logger.info("Create an order and set the initial status to 'PENDING'");
        Orders order = new Orders();
        order.setStatus(OrderStatus.PENDING);
        order = orderRepository.save(order);

        logger.info("Send order details to the queue for asynchronous processing");
        orderMessageProducer.sendOrderMessage(order.getId().toString());

        logger.info("Return order response with initial 'PENDING' status");
        return new OrderResponseDto(order.getId().intValue(), order.getStatus().name(), 0.0);
    }
    
    //Added a new function to test rate limit implementation via unit test, instead of updating current implementation
    public OrderResponseDto placeOrderRateLimit(OrderRequestDto orderRequest) {
        try{  
            logger.info("Proceeding with order processing");
            rateLimiter.acquirePermission();
            return placeOrder(orderRequest);
        } catch(RateLimitExceededException e)  {
            logger.error("Rate limit exceeded");
            throw new RateLimitExceededException("Rate limit exceeded, please try again later.");
        }
    }

    //Place an order
    @Transactional(rollbackFor = Exception.class)
    public OrderResponseDto placeOrder(OrderRequestDto request) {
        logger.info("Placing order function");
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderRequestDto.ProductItem item : request.getItems()) {
            //Product product = productRepository.findById((long) item.getProductId())
            // .orElseThrow(() -> new ProductNotFoundException("Product not found: " + item.getProductId()));
            Product product = inventoryService.getProductForUpdate((long) item.getProductId()); // Pessimistic locking
                   
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

    //Update order status
    @Transactional
    public OrderStatusResponseDto updateOrderStatus(Long orderId, OrderStatusRequestDto request) {
        logger.info("Inside updateOrderStatus() ");
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
            OrderStatus.valueOf(status);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
