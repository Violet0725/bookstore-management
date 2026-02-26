package com.bookstore.repository;

import com.bookstore.entity.Sale;
import com.bookstore.dto.PerformanceMetricsDTO.TopBookDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * SaleRepository - Data Access Layer for Sale Entity
 * 
 * Contains custom queries for analytics and aggregation
 * 
 * Interview Points:
 * - @Query: Write custom JPQL (Java Persistence Query Language) queries
 * - Aggregate functions: SUM, COUNT, AVG, MAX, MIN
 * - GROUP BY for grouping results by category
 * - Native SQL vs JPQL: JPQL is database-agnostic
 */
@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    /**
     * Get total revenue from all sales
     */
    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s")
    BigDecimal getTotalRevenue();
    
    /**
     * Get total number of sales
     */
    @Query("SELECT COUNT(s) FROM Sale s")
    Long getTotalSalesCount();
    
    /**
     * Get total quantity of books sold
     */
    @Query("SELECT COALESCE(SUM(s.quantitySold), 0) FROM Sale s")
    Long getTotalBooksSold();
    
    /**
     * Get top selling books by quantity with book details
     */
    @Query("SELECT new com.bookstore.dto.PerformanceMetricsDTO$TopBookDTO " +
           "(b.id, b.title, b.author, SUM(s.quantitySold), SUM(s.totalAmount)) " +
           "FROM Sale s JOIN Book b ON s.bookId = b.id " +
           "GROUP BY b.id, b.title, b.author " +
           "ORDER BY SUM(s.quantitySold) DESC")
    List<TopBookDTO> findTopSellingBooks();
    
    /**
     * Get sales by date range
     */
    @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    List<Sale> getSalesByDateRange(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get total revenue by date range
     */
    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    BigDecimal getRevenueByDateRange(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
}
