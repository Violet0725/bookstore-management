package com.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * BookDTO - Data Transfer Object for Book entity
 * 
 * DTOs are used to transfer data between layers (API, Service, Repository)
 * They help:
 * - Hide internal entity structure from API consumers
 * - Control what data is exposed (security)
 * - Decouple API from database schema
 * 
 * Interview Points:
 * - Why use DTOs instead of entities directly in controllers?
 * - Explain the difference between Entity (database) and DTO (API)
 * - Lombok @Data generates getters, setters, equals, hashCode, toString
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private Integer stockQuantity;
}
