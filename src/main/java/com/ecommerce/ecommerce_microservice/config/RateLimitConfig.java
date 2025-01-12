package com.ecommerce.ecommerce_microservice.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;

@Configuration
public class RateLimitConfig {
    @Bean
    public RateLimiter rateLimiter() {
        // Configure the RateLimiter
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(5) // Allow 5 requests per period
                .limitRefreshPeriod(Duration.ofSeconds(1)) // Refresh rate every second
                .timeoutDuration(Duration.ofMillis(500)) // Timeout for waiting on a permit
                .build();

        return RateLimiter.of("orderRateLimiter", config);
    }
}
