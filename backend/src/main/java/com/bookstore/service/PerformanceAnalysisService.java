package com.bookstore.service;

import com.bookstore.dto.PerformanceMetricsDTO;
import com.bookstore.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * PerformanceAnalysisService - Analytics & Reporting
 * 
 * Calculates bookstore performance metrics:
 * - Total revenue
 * - Total number of sales
 * - Total books sold
 * - Top selling books
 * 
 * Interview Points:
 * - @Transactional(readOnly = true): Optimized for read operations
 * - Custom JPQL queries in repositories
 * - Aggregating data into DTOs
 * - BigDecimal for precise financial calculations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceAnalysisService {
    
    private final SaleRepository saleRepository;
    
    /**
     * Get complete performance summary
     * 
     * Aggregates sales data into meaningful metrics
     */
    @Transactional(readOnly = true)
    public PerformanceMetricsDTO getPerformanceSummary() {
        log.info("Calculating performance summary");
        
        // Get totals
        BigDecimal totalRevenue = saleRepository.getTotalRevenue();
        Long totalSales = saleRepository.count();
        Long totalBooksSold = saleRepository.getTotalBooksSold();
        
        // Get top selling books
        List<PerformanceMetricsDTO.TopBookDTO> topBooks = saleRepository.findTopSellingBooks();
        
        return PerformanceMetricsDTO.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .totalSales(totalSales != null ? totalSales : 0L)
                .totalBooksSold(totalBooksSold != null ? totalBooksSold : 0L)
                .topSellingBooks(topBooks)
                .build();
    }
    
    /**
     * Get revenue within a specific date range
     */
    @Transactional(readOnly = true)
    public BigDecimal getRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Calculating revenue from {} to {}", startDate, endDate);
        
        BigDecimal revenue = saleRepository.getRevenueByDateRange(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
}
