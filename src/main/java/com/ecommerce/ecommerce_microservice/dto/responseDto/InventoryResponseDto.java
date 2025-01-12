package com.ecommerce.ecommerce_microservice.dto.responseDto;

import lombok.Data;

@Data
public class InventoryResponseDto {
    private int productId;
    private String name;
    private int stockQuantity;

    public InventoryResponseDto(int productId, String name, int stockQuantity) {
        this.productId = productId;
        this.name = name;
        this.stockQuantity = stockQuantity;
    }
}
