package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Book Entity - Represents a book in the bookstore inventory
 * 
 * JPA (Java Persistence API) is used for ORM (Object-Relational Mapping)
 * This maps the Java class to a database table
 * 
 * Key Annotations:
 * - @Entity: Marks this class as a JPA entity (maps to a database table)
 * - @Table: Specifies the table name (optional if class name matches table name)
 * - @Id: Marks the primary key
 * - @GeneratedValue: Configures auto-generation of primary key values
 * - @Column: Maps field to column, allows customization (nullable, length, etc.)
 * 
 * Interview Points:
 * - Explain JPA vs Hibernate: JPA is the specification, Hibernate is the implementation
 * - Why use @GeneratedValue(strategy = GenerationType.IDENTITY) for auto-increment
 * - Lombok reduces boilerplate - mention @Data, @NoArgsConstructor, @AllArgsConstructor
 */
@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    
    /**
     * Primary key - auto-generated using IDENTITY strategy
     * IDENTITY lets the database handle auto-increment (SERIAL in PostgreSQL)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Book title - required field, max 200 characters
     */
    @Column(nullable = false, length = 200)
    private String title;
    
    /**
     * Author name - required field, max 100 characters
     */
    @Column(nullable = false, length = 100)
    private String author;
    
    /**
     * ISBN (International Standard Book Number) - unique identifier
     * Could add @UniqueConstraint in table definition for database-level enforcement
     */
    @Column(unique = true, length = 13)
    private String isbn;
    
    /**
     * Price - using BigDecimal for accurate monetary calculations
     * Never use float/double for money! Precision issues.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    /**
     * Current stock quantity - cannot be negative
     */
    @Column(nullable = false)
    private Integer stockQuantity = 0;
}
