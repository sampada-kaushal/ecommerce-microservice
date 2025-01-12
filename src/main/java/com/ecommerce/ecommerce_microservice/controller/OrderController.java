package com.ecommerce.ecommerce_microservice.controller;

import com.ecommerce.ecommerce_microservice.dto.requestDto.OrderRequestDto;
import com.ecommerce.ecommerce_microservice.dto.requestDto.OrderStatusRequestDto;
import com.ecommerce.ecommerce_microservice.dto.responseDto.OrderResponseDto;
import com.ecommerce.ecommerce_microservice.dto.responseDto.OrderStatusResponseDto;
import com.ecommerce.ecommerce_microservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Controller class for Order Management
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(@RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.ok(orderService.placeOrder(orderRequestDto));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderStatusResponseDto> updateStatus(@PathVariable Long orderId,
            @RequestBody OrderStatusRequestDto statusRequestDto) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, statusRequestDto));
    }
}
