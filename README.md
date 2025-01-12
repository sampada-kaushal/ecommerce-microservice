# Order Management System Microservice


## Overview
This project implements a scalable Order Management System (OMS) microservice for an e-commerce platform. The service enables efficient handling of orders, inventory management, and order status updates using a microservices architecture to ensure scalability, resilience, and maintainability. The system is designed to handle high traffic during peak shopping seasons by incorporating strategies such as queue-based order processing and rate-limiting mechanisms.

## Problem Statement
Context: The existing monolithic application faces scalability challenges, particularly with the Order Management System (OMS) during peak shopping seasons like Black Friday. This microservice is designed to address these scalability, resilience, and maintainability issues.

## Features
- Order Placement: Validates inventory and processes new orders.
- Order Status Updates: Updates order statuses (e.g., "Pending," "Shipped," "Delivered").
- Inventory Management: Ensures inventory synchronization and prevents overselling.
- Error Handling: Robust mechanisms for failed orders and meaningful error responses.
- Scalability: Handles traffic spikes with queue-based order processing.

---

## Tech Stack
1. **Language**: Java (Spring Boot)
2. **Database**: MySQL (AWS RDS)
3. **API Documentation**: Postman collection
4. **Deployment**: AWS EC2
5. **Queue**: RabbitMQ
6. **Rate Limiting**: Rate-limiting implementation with resilience4j-ratelimiter
7. **Error Handling**: Custom exception handling with meaningful error responses
---

## Local Setup

### 1. Clone the Repository
```bash
git clone <repository_url>
cd ecommerce-microservice
```

### 2. Set Up Database (AWS RDS) - if needed
Host: <RDS_HOSTNAME>
User: <RDS_USERNAME>
Password: <RDS_PASSWORD>
Set the database connection in application.yml.

Import the connection details on MySQL workbench and it should look like below screenshots-


### 3. Install and run RabbitMQ server (MacOS)
```bash
brew install rabbitmq
brew services start rabbitmq 
```
### 4. Run the Application
```bash
mvn spring-boot:run
```

## API Documentation

- Entire API documentation can be accessed from here - https://documenter.getpostman.com/view/7235087/2sAYQWKDUf
- EC2 hostname = 54.159.196.251:8080
- Import postman collection json in repo for direct import on postman app - Ecommerce Microservice.postman_collection.json

---

## Directory Structure
```
ecommerce-microservice/
├── src/
│   ├── main/
│   │   ├── java/com/ecommerce/ecommerce_microservice
│   │   │   ├── config/
│   │   │   │   ├── RabbitConfig.java
│   │   │   │   ├── RateLimitConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── OrderController.java
│   │   │   │   └── InventoryController.java
│   │   │   ├── dto/
|   |   |   |   ├── requestDto/
│   │   │   │   |   ├── OrderRequestDto.java
│   │   │   │   |   ├── OrderStatusRequestDto.java
│   │   │   │   ├── responseDto/
│   │   │   │   |   └── InventoryResponseDto.java
│   │   │   │   |   ├── OrderResponseDto.java
│   │   │   │   |   ├── OrderStatusResponseDto.java
│   │   │   ├── entity/
│   │   │   │   ├── Orders.java
│   │   │   │   ├── OrderItem.java
│   │   │   │   ├── OrderStatus.java (enum)
│   │   │   │   └── Product.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ProductNotFoundException.java
│   │   │   │   ├── OrderNotFoundException.java
│   │   │   │   ├── InvalidOrderStatusException.java
│   │   │   │   ├── InvalidInputException.java
│   │   │   │   └── InsufficientInventoryException.java
│   │   │   │   └── RateLimitExceededException.java
│   │   │   ├── repository/
│   │   │   │   ├── OrderRepository.java
│   │   │   │   ├── ProductRepository.java
│   │   │   │   └── OrderItemRepository.java
│   │   │   ├── service/
│   │   │   │   ├── OrderService.java
│   │   │   │   ├── OrderMessageConsumer.java
│   │   │   │   ├── OrderMessageProducer.java
│   │   │   │   └── InventoryService.java
│   │   │   ├── util/
│   │   │   │   └── ApiResponse.java
│   │   │   ├── EcommerceMicroserviceApplication.java (Springboot Runner class)
│   │   ├── resources/
│   │   │   ├── application.yml
│   ├── test/ (Unit tests)
│   │   ├── java/com/ecommerce/ecommerce_microservice
│   │   │   ├── EcommerceMicroserviceApplicationTests.java
├── pom.xml
└── README.md
```

---

## Edge cases covered
Below edge cases are covered in the code and are handled by ```GlobalExceptionHandler``` (can also be seen in Postman examples):
- For getting product inventory only valid product ID can be used.
- For creating a new product, form validation are added.
- For updating product stock negative values are exempted and stock should add on to current value.
- While placing order checking validity of product id and availability of product in the inventory handled by InsufficientStock exception.
- While updating order status, checking validity of orderID and status enum.

## Extra checks added
In addition to above, later have added ```EcommerceMicroserviceApplicationTests``` class and seaprate functions to test: 
- concurrent placement of orders
- placeOrderRateLimit for rate limiting of orders.
- placeOrderQueue for sending Order requests by creation of OrderMessageProducer and OrderMessageConsumer. 
- below screenshot tells basic run of unit tests.

## Diagram
Below is very raw and basic diagram of the system implemented-


## Local and EC2 Run Screenshot
Below are screenshots of local run and EC2 instance run-



## Scalabiling strategies and future Enhancements
- Caching: We can use Redis for frequently accessed data based on user search and order history or recommendation engine. We can cache products or static data like OrderStatus.
- Monitoring: Add observability with tools like Prometheus and Grafana.
- CI/CD: Automate deployments with Jenkins or GitHub Actions.
- DB optimisation: We can improve query performance for most searched fields such as Order.id, Product.name, or OrderStatus by adding indexes to columns used in WHERE, JOIN, or sorting operations.
- We can add do Partitioning by splitting large tables into smaller, more manageable pieces like- 
    - Use partitioning for orders based on createdDate or for products based on category.
- For read-heavy operations, we can store precomputed values (like totalOrderValue in Orders) to avoid repeated calculations. 
- We can use connection pooling to efficiently manage database connections and reduce latency.
- We can use a distributed database (e.g., CockroachDB) or search system (e.g., Elasticsearch) for handling catalog search at scale.
- Horizontal Scaling: Use a load balancer (e.g. AWS ELB) to distribute traffic across multiple application or database servers.
- We can add Read Replicas for handling heavy read traffic.
    - Route all SELECT queries for Product to replicas, while writes go to the master database.   
