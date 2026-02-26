/**
 * Book Model - TypeScript interface matching Java Book entity
 * 
 * This interface defines the structure for book data transferred between
 * Angular frontend and Spring Boot backend.
 * 
 * Interview Points:
 * - TypeScript interfaces for type safety
 * - Matching Java DTO structure exactly
 * - Using number for IDs (Java Long maps to number in TypeScript)
 */
export interface Book {
  id?: number;
  title: string;
  author: string;
  isbn: string;
  price: number;
  stockQuantity: number;
}

/**
 * For creating/updating a book - ID is optional (not required for create)
 */
export type BookCreate = Omit<Book, 'id'>;
export type BookUpdate = Book;
