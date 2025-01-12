package com.ecommerce.ecommerce_microservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ecommerce.ecommerce_microservice.dto.requestDto.OrderRequestDto;
import com.ecommerce.ecommerce_microservice.dto.responseDto.OrderResponseDto;
import com.ecommerce.ecommerce_microservice.entity.OrderStatus;
import com.ecommerce.ecommerce_microservice.entity.Orders;
import com.ecommerce.ecommerce_microservice.entity.Product;
import com.ecommerce.ecommerce_microservice.exception.InsufficientInventoryException;
import com.ecommerce.ecommerce_microservice.exception.RateLimitExceededException;
import com.ecommerce.ecommerce_microservice.repository.OrderRepository;
import com.ecommerce.ecommerce_microservice.repository.ProductRepository;
import com.ecommerce.ecommerce_microservice.service.OrderService;

@SpringBootTest
class EcommerceMicroserviceApplicationTests {
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderService orderService;

	@Test
	void contextLoads() {
	}

	@Test
	public void testConcurrentOrderPlacement() throws InterruptedException {
		Product product = new Product();
		product.setName("Test Product");
		product.setDescription("Description");
		product.setPrice(BigDecimal.TEN);
		product.setStockQuantity(2);
		productRepository.save(product);

		OrderRequestDto orderRequest1 = new OrderRequestDto();
		// Add product with quantity 1 for orderRequest1
		List<OrderRequestDto.ProductItem> items1 = new ArrayList<>();
		items1.add(new OrderRequestDto.ProductItem(1, 1)); // productId=1, quantity=1
		orderRequest1.setItems(items1);

		OrderRequestDto orderRequest2 = new OrderRequestDto();
		List<OrderRequestDto.ProductItem> items2 = new ArrayList<>();
		items2.add(new OrderRequestDto.ProductItem(1, 1)); // productId=1, quantity=1
		orderRequest2.setItems(items2);
		// Add product with quantity 1 for orderRequest2

		// Concurrent order placements
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		Future<OrderResponseDto> response1 = executorService.submit(() -> orderService.placeOrder(orderRequest1));
		Future<OrderResponseDto> response2 = executorService.submit(() -> orderService.placeOrder(orderRequest2));

		// Wait for both responses
		try {
			response1.get();
			response2.get();
		} catch (ExecutionException e) {
			System.out.println("Caught ExecutionException: " + e.getClass());
		}

		// Assert that only one order went through, and stock is updated
		Product updatedProduct = productRepository.findById(product.getId())
				.orElseThrow(() -> new RuntimeException("Product not found"));
		assertEquals(0, updatedProduct.getStockQuantity()); // Both orders should have reduced stock to 0

		// Check if InsufficientInventoryException was thrown
		try {
			// Attempt to place an order after stock is exhausted
			OrderRequestDto orderRequest3 = new OrderRequestDto();
			List<OrderRequestDto.ProductItem> items3 = new ArrayList<>();
			items3.add(new OrderRequestDto.ProductItem(1, 1)); // productId=1, quantity=1
			orderRequest3.setItems(items3);

			// This should throw an InsufficientInventoryException
			orderService.placeOrder(orderRequest3);
			fail("Expected InsufficientInventoryException to be thrown.");
		} catch (InsufficientInventoryException e) {
		}
	}

	@Test
	public void testAsyncOrderProcessing() throws InterruptedException {
		OrderRequestDto orderRequest = new OrderRequestDto();
		// Add items to orderRequestDto as needed
		orderRequest.setItems(new ArrayList<>());
		orderRequest.getItems().add(new OrderRequestDto.ProductItem(1, 1));

		// Place the order via the service
		OrderResponseDto response = orderService.placeOrderQueue(orderRequest);

		// Assert that the initial order response status is 'PENDING'
		assertEquals("PENDING", response.getStatus());
		Thread.sleep(5000); // Wait for the queue to process

		// Fetch the updated order from the repository
		Orders order = orderRepository.findById((long) response.getOrderId()).orElseThrow();

		// Assert that the order status is updated to 'SHIPPED' after processing
		assertEquals(OrderStatus.SHIPPED, order.getStatus());
	}

	@Test
	void testRateLimit() throws InterruptedException {
		// Add details to orderRequestDto as needed
		OrderRequestDto orderRequest = new OrderRequestDto();
		orderRequest.setItems(new ArrayList<>());
		orderRequest.getItems().add(new OrderRequestDto.ProductItem(1, 1));

		// Executor service to simulate multiple concurrent order requests
		ExecutorService executor = Executors.newFixedThreadPool(10);

		// Submit 10 requests, this should trigger rate limiting
		for (int i = 0; i < 10; i++) {
			executor.submit(() -> {
				try {
					orderService.placeOrderRateLimit(orderRequest);
				} catch (RateLimitExceededException e) {
					// Verify that rate limiting exception is thrown
					assertTrue(e.getMessage().contains("Rate limit exceeded"));
				}
			});
		}

		// Wait for all tasks to finish
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.SECONDS);
	}
}
