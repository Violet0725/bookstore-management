package com.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SaleDTO - Data Transfer Object for Sale entity
 * 
 * Used for creating new sales and returning sale data to clients
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;  // For displaying book name in responses
    private Integer quantitySold;
    private LocalDateTime saleDate;
    private BigDecimal totalAmount;
}
