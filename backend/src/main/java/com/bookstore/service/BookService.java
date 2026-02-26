package com.bookstore.service;

import com.bookstore.dto.BookDTO;
import com.bookstore.entity.Book;
import com.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BookService - Business Logic Layer for Book Management
 * 
 * This service handles:
 * - CRUD operations for books
 * - Conversion between Entity and DTO
 * - Triggering WebSocket alerts when stock is low
 * 
 * Interview Points:
 * - @Service: Marks this as a Spring-managed bean (component for business logic)
 * - @Transactional: Ensures database operations are atomic (all-or-nothing)
 * - @RequiredArgsConstructor + Lombok: Dependency injection without explicit @Autowired
 * - Explain the service layer pattern: sits between controllers and repositories
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    
    private final BookRepository bookRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    // Low stock threshold - when to send alerts
    private static final int LOW_STOCK_THRESHOLD = 5;
    
    /**
     * Get all books
     * Returns list of BookDTOs
     */
    public List<BookDTO> getAllBooks() {
        log.debug("Fetching all books");
        return bookRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get a book by ID
     */
    public BookDTO getBookById(Long id) {
        log.debug("Fetching book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        return convertToDTO(book);
    }
    
    /**
     * Create a new book
     * @Transactional ensures the save is atomic
     */
    @Transactional
    public BookDTO createBook(BookDTO bookDTO) {
        log.debug("Creating new book: {}", bookDTO.getTitle());
        Book book = convertToEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        log.info("Book created with id: {}", savedBook.getId());
        return convertToDTO(savedBook);
    }
    
    /**
     * Update an existing book
     */
    @Transactional
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        log.debug("Updating book with id: {}", id);
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        
        // Update fields
        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setAuthor(bookDTO.getAuthor());
        existingBook.setIsbn(bookDTO.getIsbn());
        existingBook.setPrice(bookDTO.getPrice());
        existingBook.setStockQuantity(bookDTO.getStockQuantity());
        
        Book updatedBook = bookRepository.save(existingBook);
        log.info("Book updated with id: {}", updatedBook.getId());
        return convertToDTO(updatedBook);
    }
    
    /**
     * Delete a book by ID
     */
    @Transactional
    public void deleteBook(Long id) {
        log.debug("Deleting book with id: {}", id);
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
        log.info("Book deleted with id: {}", id);
    }
    
    /**
     * Process a sale - reduces stock and checks for low stock alert
     * This is called after a successful sale
     */
    @Transactional
    public void processSale(Long bookId, int quantitySold) {
        log.debug("Processing sale for book id: {}, quantity: {}", bookId, quantitySold);
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
        // Reduce stock
        int newStock = book.getStockQuantity() - quantitySold;
        book.setStockQuantity(Math.max(0, newStock)); // Ensure stock doesn't go negative
        bookRepository.save(book);
        
        // Check for low stock and send WebSocket alert
        if (book.getStockQuantity() < LOW_STOCK_THRESHOLD) {
            sendLowStockAlert(book);
        }
        
        log.info("Sale processed. New stock for book {}: {}", bookId, book.getStockQuantity());
    }
    
    /**
     * Send low stock alert via WebSocket
     * Uses SimpMessagingTemplate to send to /topic/low-stock
     */
    private void sendLowStockAlert(Book book) {
        log.warn("LOW STOCK ALERT: Book '{}' (ID: {}) has only {} copies left!", 
                book.getTitle(), book.getId(), book.getStockQuantity());
        
        // Create alert message
        String alertMessage = String.format(
                "Low Stock Alert: \"%s\" by %s has only %d copies remaining!", 
                book.getTitle(), book.getAuthor(), book.getStockQuantity()
        );
        
        // Send to WebSocket topic
        messagingTemplate.convertAndSend("/topic/low-stock", alertMessage);
        log.debug("Low stock alert sent via WebSocket");
    }
    
    /**
     * Convert Book entity to BookDTO
     */
    private BookDTO convertToDTO(Book book) {
        return new BookDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPrice(),
                book.getStockQuantity()
        );
    }
    
    /**
     * Convert BookDTO to Book entity
     */
    private Book convertToEntity(BookDTO bookDTO) {
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setPrice(bookDTO.getPrice());
        book.setStockQuantity(bookDTO.getStockQuantity());
        return book;
    }
}
