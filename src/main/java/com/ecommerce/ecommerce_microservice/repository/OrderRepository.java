package com.ecommerce.ecommerce_microservice.repository;

import com.ecommerce.ecommerce_microservice.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {
}
