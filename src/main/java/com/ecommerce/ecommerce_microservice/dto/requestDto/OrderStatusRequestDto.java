package com.ecommerce.ecommerce_microservice.dto.requestDto;

import lombok.Data;

@Data
public class OrderStatusRequestDto {
   private String status;
   public OrderStatusRequestDto(String status){
    this.status=status;
   }
}
