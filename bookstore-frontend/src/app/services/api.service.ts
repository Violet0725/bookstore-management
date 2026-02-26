import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Book, BookCreate, BookUpdate } from '../models/book.model';
import { Sale, SaleCreate } from '../models/sale.model';
import { PerformanceMetrics, TopBook } from '../models/performance-metrics.model';

/**
 * ApiService - HTTP Communication with Spring Boot Backend
 * 
 * Provides all HTTP calls to the REST API using Angular's HttpClient.
 * 
 * Interview Points:
 * - HttpClient returns Observables (reactive programming)
 * - Generic types ensure type safety
 * - Dependency injection of HttpClient via provideHttpClient()
 * - RxJS operators for transformation (map, catchError)
 */
@Injectable({
  providedIn: 'root'
})
export class ApiService {
  // Base URL for the Spring Boot API
  private readonly API_URL = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // ==================== Book Endpoints ====================

  /**
   * GET /api/books - Get all books
   */
  getAllBooks(): Observable<Book[]> {
    return this.http.get<Book[]>(`${this.API_URL}/books`);
  }

  /**
   * GET /api/books/{id} - Get book by ID
   */
  getBookById(id: number): Observable<Book> {
    return this.http.get<Book>(`${this.API_URL}/books/${id}`);
  }

  /**
   * POST /api/books - Create new book
   */
  createBook(book: BookCreate): Observable<Book> {
    return this.http.post<Book>(`${this.API_URL}/books`, book);
  }

  /**
   * PUT /api/books/{id} - Update book
   */
  updateBook(id: number, book: BookUpdate): Observable<Book> {
    return this.http.put<Book>(`${this.API_URL}/books/${id}`, book);
  }

  /**
   * DELETE /api/books/{id} - Delete book
   */
  deleteBook(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/books/${id}`);
  }

  /**
   * POST /api/books/sale - Record a sale (reduces stock)
   */
  recordSale(sale: SaleCreate): Observable<Sale> {
    return this.http.post<Sale>(`${this.API_URL}/books/sale`, sale);
  }

  // ==================== Analytics Endpoints ====================

  /**
   * GET /api/analytics/summary - Get performance metrics
   */
  getPerformanceSummary(): Observable<PerformanceMetrics> {
    return this.http.get<PerformanceMetrics>(`${this.API_URL}/analytics/summary`);
  }

  /**
   * GET /api/analytics/top-books - Get top selling books
   */
  getTopSellingBooks(): Observable<TopBook[]> {
    return this.http.get<TopBook[]>(`${this.API_URL}/analytics/top-books`);
  }

  /**
   * GET /api/analytics/revenue?startDate=&endDate= - Get revenue by date range
   */
  getRevenueByDateRange(startDate: string, endDate: string): Observable<number> {
    return this.http.get<number>(
      `${this.API_URL}/analytics/revenue?startDate=${startDate}&endDate=${endDate}`
    );
  }
}
