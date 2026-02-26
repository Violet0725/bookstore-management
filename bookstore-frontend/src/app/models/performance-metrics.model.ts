/**
 * Performance Metrics Model
 * 
 * DTO for bookstore performance analytics
 */

export interface PerformanceMetrics {
  totalRevenue: number;
  totalSales: number;
  totalBooksSold: number;
  topSellingBooks: TopBook[];
}

export interface TopBook {
  bookTitle: string;
  author: string;
  totalQuantitySold: number;
  totalRevenue: number;
}
