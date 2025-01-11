package com.ecommerce.ecommerce_microservice.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommerce_microservice.dto.responseDto.InventoryResponseDto;
import com.ecommerce.ecommerce_microservice.entity.Product;
import com.ecommerce.ecommerce_microservice.exception.InvalidInputException;
import com.ecommerce.ecommerce_microservice.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    @Autowired
    InventoryService inventoryService;

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponseDto> getProductInventory(@PathVariable int productId) {
        return ResponseEntity.ok(inventoryService.getProductInventory((long)productId));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam Integer stockQuantity) {
                   if (price == null) {
        throw new InvalidInputException("Price is a required field and must be a valid number");
    }
        return ResponseEntity.ok(inventoryService.createProduct(name, description, price, stockQuantity));
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponseDto>> getAllProducts() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @PutMapping("/{productId}/stock")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long productId,
            @RequestParam Integer newStock) {
        return ResponseEntity.ok(inventoryService.updateProductStock(productId, newStock));
    }
}
