package com.ecommerce.ecommerce_microservice.repository;

import com.ecommerce.ecommerce_microservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
