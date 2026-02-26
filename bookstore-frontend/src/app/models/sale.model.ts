/**
 * Sale Model - TypeScript interface matching Java Sale entity
 */
export interface Sale {
  id?: number;
  bookId: number;
  bookTitle?: string;  // For displaying book name in responses
  quantitySold: number;
  saleDate: string;   // ISO date string
  totalAmount: number;
}

/**
 * For creating a new sale
 */
export interface SaleCreate {
  bookId: number;
  quantitySold: number;
}
