package com.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

/**
 * PerformanceMetricsDTO - Data Transfer Object for analytics data
 * 
 * Aggregated metrics for bookstore performance analysis
 * This will be returned by the analytics endpoints
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceMetricsDTO {
    
    /**
     * Total revenue from all sales
     */
    private BigDecimal totalRevenue;
    
    /**
     * Total number of sales transactions
     */
    private Long totalSales;
    
    /**
     * Total quantity of books sold
     */
    private Long totalBooksSold;
    
    /**
     * Top selling books with their sales count
     */
    private List<TopBookDTO> topSellingBooks;
    
    /**
     * Inner class for top-selling book data
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopBookDTO {
        private Long bookId;
        private String bookTitle;
        private String author;
        private Long totalQuantitySold;
        private BigDecimal totalRevenue;
    }
}
