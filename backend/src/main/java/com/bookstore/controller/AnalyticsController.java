package com.bookstore.controller;

import com.bookstore.dto.PerformanceMetricsDTO;
import com.bookstore.service.PerformanceAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AnalyticsController - REST API Endpoints for Performance Analytics
 * 
 * Provides endpoints for bookstore performance metrics:
 * - GET /api/analytics/summary - Get complete performance summary
 * - GET /api/analytics/revenue?startDate=&endDate= - Get revenue by date range
 * 
 * Interview Points:
 * - Explain the analytics/metrics pattern in REST APIs
 * - Query parameters vs path variables: when to use each
 * - @DateTimeFormat for parsing date parameters
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class AnalyticsController {
    
    private final PerformanceAnalysisService performanceAnalysisService;
    
    /**
     * GET /api/analytics/summary
     * Get complete performance metrics summary
     * 
     * Returns aggregated data including:
     * - Total revenue
     * - Total number of sales
     * - Total books sold
     * - Top selling books
     */
    @GetMapping("/summary")
    public ResponseEntity<PerformanceMetricsDTO> getPerformanceSummary() {
        log.info("REST request to get performance summary");
        PerformanceMetricsDTO metrics = performanceAnalysisService.getPerformanceSummary();
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * GET /api/analytics/top-books
     * Get top selling books (same as summary but isolated endpoint)
     */
    @GetMapping("/top-books")
    public ResponseEntity<List<PerformanceMetricsDTO.TopBookDTO>> getTopSellingBooks() {
        log.info("REST request to get top selling books");
        PerformanceMetricsDTO metrics = performanceAnalysisService.getPerformanceSummary();
        return ResponseEntity.ok(metrics.getTopSellingBooks());
    }
    
    /**
     * GET /api/analytics/revenue
     * Get revenue within a date range
     * 
     * Query parameters:
     * - startDate: Start of date range (ISO format)
     * - endDate: End of date range (ISO format)
     * 
     * Example: /api/analytics/revenue?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59
     */
    @GetMapping("/revenue")
    public ResponseEntity<BigDecimal> getRevenueByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("REST request to get revenue from {} to {}", startDate, endDate);
        BigDecimal revenue = performanceAnalysisService.getRevenueByDateRange(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }
}
