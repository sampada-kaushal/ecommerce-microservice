# Order Management System Microservice


## Overview
This project is a scalable Order Management System (OMS) microservice for an e-commerce platform. It enables efficient handling of orders, inventory management, and order status updates with a microservices architecture to ensure scalability, resilience, and maintainability.

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

---

## Installation

### 1. Clone the Repository
```bash
git clone <repository_url>
cd FASTAPI
```
## API Usage

### Endpoint: `/scrape/`
- **Method**: `GET`
- **Parameters**:
  - `pages` (int): Number of pages to scrape.
  - `proxy` (optional, str): Proxy string for scraping.
  - `token` (str): Authentication token.

### Example cURL Request
```bash
curl -X 'GET' \
  'http://127.0.0.1:8000/scrape/?pages=3&token=your_static_token' \
  -H 'accept: application/json'
```

---

## Directory Structure
```
ecommerce-microservice/
├── src/
│   ├── main/
│   │   ├── java/com/ecommerce/ecommerce_microservice
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
│   │   │   │   ├──OrderNotFoundException.java
│   │   │   │   ├──InvalidOrderStatusException.java
│   │   │   │   ├──InvalidInputException.java
│   │   │   │   └── InsufficientInventoryException.java
│   │   │   ├── repository/
│   │   │   │   ├── OrderRepository.java
│   │   │   │   ├── ProductRepository.java
│   │   │   │   └── OrderItemRepository.java
│   │   │   ├── service/
│   │   │   │   ├── OrderService.java
│   │   │   │   └── InventoryService.java
│   │   │   ├── util/
│   │   │   │   └── ApiResponse.java
│   │   │   ├── EcommerceMicroserviceApplication.java (Springboot Runner class)
│   │   ├── resources/
│   │   │   ├── application.yml
│   │   │   ├── data.sql (optional)
│   │   │   └── schema.sql (optional)
├── pom.xml
└── README.md
```

---