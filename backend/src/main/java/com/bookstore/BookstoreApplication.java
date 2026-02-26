package com.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application Entry Point
 * 
 * This is the primary class that bootstraps the Spring Boot application.
 * @SpringBootApplication is a convenience annotation that combines:
 * - @Configuration: Marks the class as a source of bean definitions
 * - @EnableAutoConfiguration: Enables Spring Boot's auto-configuration
 * - @ComponentScan: Enables component scanning for @Component, @Service, @Repository, @Controller
 * 
 * Interview Point: Explain how Spring Boot auto-configuration works and why
 * we use this single annotation instead of multiple individual annotations.
 */
@SpringBootApplication
public class BookstoreApplication {
    
    public static void main(String[] args) {
        // SpringApplication.run() is the entry point that starts the Spring Boot application
        // It creates an ApplicationContext, configures the environment, and starts the embedded server
        SpringApplication.run(BookstoreApplication.class, args);
    }
}
