package com.ecommerce.ecommerce_microservice.dto.responseDto;

import lombok.Data;

@Data
public class OrderResponseDto {
  private int orderId;
  private String status;
  private Double totalPrice;

  public OrderResponseDto(int orderId, String status, Double totalPrice) {
    this.orderId = orderId;
    this.status = status;
    this.totalPrice = totalPrice;
  }
}
