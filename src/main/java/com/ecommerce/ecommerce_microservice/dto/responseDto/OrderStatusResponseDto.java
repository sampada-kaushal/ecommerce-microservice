package com.ecommerce.ecommerce_microservice.dto.responseDto;

import lombok.Data;

@Data
public class OrderStatusResponseDto {
    private int orderId;
    private String status;

    public OrderStatusResponseDto(int orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }
}
