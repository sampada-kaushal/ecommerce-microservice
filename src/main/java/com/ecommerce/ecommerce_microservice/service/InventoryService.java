package com.ecommerce.ecommerce_microservice.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ecommerce.ecommerce_microservice.dto.responseDto.InventoryResponseDto;
import com.ecommerce.ecommerce_microservice.entity.Product;
import com.ecommerce.ecommerce_microservice.exception.InvalidInputException;
import com.ecommerce.ecommerce_microservice.exception.ProductNotFoundException;
import com.ecommerce.ecommerce_microservice.repository.ProductRepository;

@Service
public class InventoryService {
    @Autowired
    private ProductRepository productRepository;

    public InventoryResponseDto getProductInventory(Long productId) {
        //Logic for getting latest inventory for a particular product
        Product product=productRepository.findById(productId).orElseThrow(()-> new ProductNotFoundException("Product not found: "+productId));
        return new InventoryResponseDto(product.getId().intValue(), product.getName(), product.getStockQuantity());
    }
    public Product createProduct(String name, String description, BigDecimal price, Integer stockQuantity) {
        // Validate inputs
    if (name == null || name.trim().isEmpty()) {
        throw new InvalidInputException("Name is a required field");
    }
    if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
        throw new InvalidInputException("Price must be a valid number greater than zero");
    }
    
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        return productRepository.save(product);
    }
    public List<InventoryResponseDto> getAllInventory() {
        List<Product> productList=productRepository.findAll();
        List<InventoryResponseDto> inventoryResponseDtos=new ArrayList<>();
        for(Product product: productList){
            inventoryResponseDtos.add(new InventoryResponseDto(product.getId().intValue(), product.getName(), product.getStockQuantity()));
        }
        return inventoryResponseDtos;
    }

    public Product updateProductStock(Long productId, Integer newStock) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
        if(newStock<=0){
            throw new InvalidInputException("Stock count should be greater than 0");           
        }
        product.setStockQuantity(newStock);       
        return productRepository.save(product);
    }
}
