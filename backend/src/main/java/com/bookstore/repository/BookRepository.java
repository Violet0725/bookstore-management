package com.bookstore.repository;

import com.bookstore.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * BookRepository - Data Access Layer for Book Entity
 * 
 * Extends JpaRepository which provides:
 * - CRUD operations: save(), findById(), findAll(), delete(), etc.
 * - Pagination and sorting support
 * - Custom query methods through method naming conventions
 * 
 * Interview Points:
 * - Explain Spring Data JPA: reduces boilerplate significantly
 * - JpaRepository<T, ID> interface: T is entity type, ID is primary key type
 * - Custom query methods follow Spring Data naming conventions
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    /**
     * Find a book by ISBN - Spring Data creates the query from method name
     */
    Optional<Book> findByIsbn(String isbn);
    
    /**
     * Check if a book exists by ISBN
     */
    boolean existsByIsbn(String isbn);
}
