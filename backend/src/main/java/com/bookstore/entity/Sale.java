package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Sale Entity - Represents a book sale transaction
 * 
 * This entity tracks every sale made in the bookstore.
 * It stores the book reference, quantity sold, date, and total amount.
 * 
 * Interview Points:
 * - Explain the relationship between Sale and Book (Many-to-One)
 * - Why we store bookId instead of Book object - simpler, less serialization issues
 * - LocalDateTime vs Date - modern Java date/time API (java.time)
 */
@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sale {
    
    /**
     * Primary key - auto-generated
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Reference to the book that was sold
     * Using @Column to store just the ID - simpler for REST API serialization
     */
    @Column(name = "book_id", nullable = false)
    private Long bookId;
    
    /**
     * Quantity of books sold in this transaction
     */
    @Column(nullable = false)
    private Integer quantitySold;
    
    /**
     * When the sale occurred
     */
    @Column(nullable = false)
    private LocalDateTime saleDate;
    
    /**
     * Total amount for this sale (price * quantity)
     * Using BigDecimal for accurate monetary calculations
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
}
