package com.ecommerce.ecommerce_microservice.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public OrderMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // Method to send an order message
    public void sendOrderMessage(String orderId) {
        rabbitTemplate.convertAndSend("orderExchange", "order.routing.key", orderId);
    }
}
