package com.bookstore.service;

import com.bookstore.dto.SaleDTO;
import com.bookstore.entity.Book;
import com.bookstore.entity.Sale;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SaleService - Business Logic Layer for Sales Management
 * 
 * Handles:
 * - Recording new sales
 * - Updating book stock quantities
 * - Converting between Sale entity and DTO
 * 
 * Interview Points:
 * - Explain how transactions maintain data integrity
 * - Why we update book stock in the same transaction as creating the sale
 * - @Transactional isolation levels and propagation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SaleService {
    
    private final SaleRepository saleRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    
    /**
     * Get all sales
     */
    public List<SaleDTO> getAllSales() {
        log.debug("Fetching all sales");
        return saleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get a sale by ID
     */
    public SaleDTO getSaleById(Long id) {
        log.debug("Fetching sale with id: {}", id);
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found with id: " + id));
        return convertToDTO(sale);
    }
    
    /**
     * Create a new sale
     * 
     * This is a critical operation that must be transactional:
     * 1. Verify book exists and has sufficient stock
     * 2. Create the sale record
     * 3. Reduce book stock (triggers low stock alert if needed)
     */
    @Transactional
    public SaleDTO createSale(SaleDTO saleDTO) {
        log.debug("Creating new sale for book id: {}, quantity: {}", 
                saleDTO.getBookId(), saleDTO.getQuantitySold());
        
        // 1. Verify book exists and has sufficient stock
        Book book = bookRepository.findById(saleDTO.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + saleDTO.getBookId()));
        
        if (book.getStockQuantity() < saleDTO.getQuantitySold()) {
            throw new RuntimeException("Insufficient stock. Available: " + book.getStockQuantity() + 
                    ", Requested: " + saleDTO.getQuantitySold());
        }
        
        // 2. Calculate total amount
        BigDecimal totalAmount = book.getPrice().multiply(
                BigDecimal.valueOf(saleDTO.getQuantitySold()));
        
        // 3. Create and save the sale
        Sale sale = new Sale();
        sale.setBookId(book.getId());
        sale.setQuantitySold(saleDTO.getQuantitySold());
        sale.setSaleDate(LocalDateTime.now());
        sale.setTotalAmount(totalAmount);
        
        Sale savedSale = saleRepository.save(sale);
        log.info("Sale created with id: {}, total amount: {}", savedSale.getId(), totalAmount);
        
        // 4. Process the sale (reduce stock, check for low stock alert)
        bookService.processSale(book.getId(), saleDTO.getQuantitySold());
        
        return convertToDTO(savedSale, book.getTitle());
    }
    
    /**
     * Convert Sale entity to SaleDTO
     */
    private SaleDTO convertToDTO(Sale sale) {
        // Try to get book title if book exists
        String bookTitle = bookRepository.findById(sale.getBookId())
                .map(Book::getTitle)
                .orElse("Unknown Book");
        
        return convertToDTO(sale, bookTitle);
    }
    
    /**
     * Convert Sale entity to SaleDTO with known book title
     */
    private SaleDTO convertToDTO(Sale sale, String bookTitle) {
        return new SaleDTO(
                sale.getId(),
                sale.getBookId(),
                bookTitle,
                sale.getQuantitySold(),
                sale.getSaleDate(),
                sale.getTotalAmount()
        );
    }
}
