package com.ecommerce.ecommerce_microservice.repository;

import com.ecommerce.ecommerce_microservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
