package com.ecommerce.ecommerce_microservice.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.ecommerce_microservice.dto.responseDto.InventoryResponseDto;
import com.ecommerce.ecommerce_microservice.entity.Product;
import com.ecommerce.ecommerce_microservice.exception.InvalidInputException;
import com.ecommerce.ecommerce_microservice.exception.ProductNotFoundException;
import com.ecommerce.ecommerce_microservice.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class InventoryService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private ProductRepository productRepository;

    // Get stock quantity in the inventory product-wise
    public InventoryResponseDto getProductInventory(Long productId) {
        logger.info("Inside getProductInventory()");
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
        return new InventoryResponseDto(product.getId().intValue(), product.getName(), product.getStockQuantity());
    }

    // Create a new product in the DB
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
        logger.info("Product created with given properties");
        return productRepository.save(product);
    }

    // Get entire inventory
    public List<InventoryResponseDto> getAllInventory() {
        logger.info("Inside getAllInventory()");
        List<Product> productList = productRepository.findAll();
        List<InventoryResponseDto> inventoryResponseDtos = new ArrayList<>();
        for (Product product : productList) {
            inventoryResponseDtos.add(new InventoryResponseDto(product.getId().intValue(), product.getName(),
                    product.getStockQuantity()));
        }
        return inventoryResponseDtos;
    }

    // Update stock value of product
    @Transactional
    public Product updateProductStock(Long productId, Integer newStock) {
        logger.info("Inside updateProductStock()");
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
        if (newStock <= 0) {
            throw new InvalidInputException("Stock count should be greater than 0");
        }
        product.setStockQuantity(product.getStockQuantity() + newStock);
        return productRepository.save(product);
    }

    @Transactional
    public Product getProductForUpdate(Long productId) {
        // Fetch the product using pessimistic locking for updates
        return productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
    }
}
