package com.ecommerce.ecommerce_microservice.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.ecommerce_microservice.entity.OrderStatus;
import com.ecommerce.ecommerce_microservice.entity.Orders;
import com.ecommerce.ecommerce_microservice.repository.OrderRepository;

@Service
public class OrderMessageConsumer {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderMessageConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @RabbitListener(queues = "orderQueue")
    public void processOrder(String orderId) {
        // Fetch order by ID and update status to 'SHIPPED'
        Orders order = orderRepository.findById(Long.valueOf(orderId)).orElseThrow();
        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);
    }
}