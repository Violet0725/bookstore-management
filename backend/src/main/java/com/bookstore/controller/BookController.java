package com.bookstore.controller;

import com.bookstore.dto.BookDTO;
import com.bookstore.dto.SaleDTO;
import com.bookstore.service.BookService;
import com.bookstore.service.SaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * BookController - REST API Endpoints for Book Management
 * 
 * Exposes CRUD operations via HTTP methods:
 * - GET /api/books - Get all books
 * - GET /api/books/{id} - Get book by ID
 * - POST /api/books - Create new book
 * - PUT /api/books/{id} - Update book
 * - DELETE /api/books/{id} - Delete book
 * - POST /api/books/sale - Record a sale
 * 
 * Interview Points:
 * - @RestController: Combines @Controller and @ResponseBody (returns JSON, not view)
 * - @RequestMapping: Defines base URL path for all endpoints
 * - @GetMapping, @PostMapping, @PutMapping, @DeleteMapping: HTTP method annotations
 * - @PathVariable: Extracts URL path variables (e.g., {id})
 * - @RequestBody: Deserializes JSON request body to Java object
 * - ResponseEntity: Gives control over HTTP status codes
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class BookController {
    
    private final BookService bookService;
    private final SaleService saleService;
    
    /**
     * GET /api/books
     * Get all books in the inventory
     */
    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        log.info("REST request to get all books");
        List<BookDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }
    
    /**
     * GET /api/books/{id}
     * Get a specific book by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        log.info("REST request to get book with id: {}", id);
        BookDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }
    
    /**
     * POST /api/books
     * Create a new book
     */
    @PostMapping
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO bookDTO) {
        log.info("REST request to create new book: {}", bookDTO.getTitle());
        BookDTO createdBook = bookService.createBook(bookDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }
    
    /**
     * PUT /api/books/{id}
     * Update an existing book
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        log.info("REST request to update book with id: {}", id);
        BookDTO updatedBook = bookService.updateBook(id, bookDTO);
        return ResponseEntity.ok(updatedBook);
    }
    
    /**
     * DELETE /api/books/{id}
     * Delete a book
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.info("REST request to delete book with id: {}", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * POST /api/books/sale
     * Record a new sale (reduces stock)
     * 
     * This endpoint handles the sale transaction:
     * 1. Validates book exists and has sufficient stock
     * 2. Creates sale record
     * 3. Reduces book stock
     * 4. Triggers low stock alert if needed
     */
    @PostMapping("/sale")
    public ResponseEntity<SaleDTO> recordSale(@RequestBody SaleDTO saleDTO) {
        log.info("REST request to record sale for book id: {}, quantity: {}", 
                saleDTO.getBookId(), saleDTO.getQuantitySold());
        
        SaleDTO sale = saleService.createSale(saleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(sale);
    }
}
