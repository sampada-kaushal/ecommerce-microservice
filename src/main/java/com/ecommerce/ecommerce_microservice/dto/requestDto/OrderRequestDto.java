package com.ecommerce.ecommerce_microservice.dto.requestDto;

import java.util.List;

import lombok.Data;

@Data
public class OrderRequestDto {
    int userId;
    List<ProductItem> items;
    @Data
    public static class ProductItem{
        private int productId;
        private int quantity;
    }
}
