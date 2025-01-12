package com.ecommerce.ecommerce_microservice.repository;

import com.ecommerce.ecommerce_microservice.entity.Product;

import jakarta.persistence.LockModeType;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findByIdForUpdate(@Param("productId") Long productId);
}
