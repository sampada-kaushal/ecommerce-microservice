package com.ecommerce.ecommerce_microservice.config;

import org.springframework.amqp.core.Queue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Configuration
public class RabbitConfig {

    // Define a queue
    @Bean
    public Queue queue() {
        return new Queue("orderQueue", true); // Queue name, durable
    }

    // Define a topic exchange
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("orderExchange");
    }

    // Bind the queue to the exchange
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with("order.#");
    }

    // Set up RabbitTemplate for sending messages
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}