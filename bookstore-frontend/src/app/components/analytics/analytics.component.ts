import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ApiService } from '../../services/api.service';
import { WebSocketService } from '../../services/websocket.service';
import { PerformanceMetrics, TopBook } from '../../models/performance-metrics.model';

/**
 * AnalyticsDashboardComponent - Performance Metrics Display
 * 
 * Shows bookstore performance analytics:
 * - Total revenue
 * - Total sales count
 * - Total books sold
 * - Top selling books
 * 
 * Also displays real-time low stock alerts via WebSocket.
 * 
 * Interview Points:
 * - Angular Material cards for dashboard layout
 * - WebSocket for real-time notifications
 * - RxJS for reactive data handling
 * - Component lifecycle hooks (OnInit)
 */
@Component({
  selector: 'app-analytics-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatSnackBarModule,
    MatTableModule,
    MatProgressSpinnerModule
  ],
  template: `
    <div class="dashboard-container">
      <h1>Performance Dashboard</h1>
      
      <!-- Loading indicator -->
      <div *ngIf="isLoading" class="loading">
        <mat-spinner diameter="50"></mat-spinner>
      </div>
      
      <!-- Metrics Cards -->
      <div class="metrics-grid" *ngIf="!isLoading">
        <!-- Total Revenue Card -->
        <mat-card class="metric-card revenue">
          <mat-card-header>
            <mat-icon mat-card-avatar>attach_money</mat-icon>
            <mat-card-title>Total Revenue</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div class="metric-value">{{ metrics?.totalRevenue | currency }}</div>
          </mat-card-content>
        </mat-card>
        
        <!-- Total Sales Card -->
        <mat-card class="metric-card sales">
          <mat-card-header>
            <mat-icon mat-card-avatar>shopping_cart</mat-icon>
            <mat-card-title>Total Sales</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div class="metric-value">{{ metrics?.totalSales }}</div>
          </mat-card-content>
        </mat-card>
        
        <!-- Books Sold Card -->
        <mat-card class="metric-card books">
          <mat-card-header>
            <mat-icon mat-card-avatar>menu_book</mat-icon>
            <mat-card-title>Books Sold</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div class="metric-value">{{ metrics?.totalBooksSold }}</div>
          </mat-card-content>
        </mat-card>
        
        <!-- Average Sale Card -->
        <mat-card class="metric-card average">
          <mat-card-header>
            <mat-icon mat-card-avatar>trending_up</mat-icon>
            <mat-card-title>Avg. Sale Value</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div class="metric-value">{{ averageSaleValue | currency }}</div>
          </mat-card-content>
        </mat-card>
      </div>
      
      <!-- Top Selling Books -->
      <mat-card class="top-books-card" *ngIf="!isLoading">
        <mat-card-header>
          <mat-card-title>Top Selling Books</mat-card-title>
          <button mat-icon-button (click)="refreshData()">
            <mat-icon>refresh</mat-icon>
          </button>
        </mat-card-header>
        <mat-card-content>
          <table mat-table [dataSource]="metrics?.topSellingBooks || []" class="top-books-table">
            <!-- Rank Column -->
            <ng-container matColumnDef="rank">
              <th mat-header-cell *matHeaderCellDef>#</th>
              <td mat-cell *matCellDef="let i = index">{{ i + 1 }}</td>
            </ng-container>
            
            <!-- Title Column -->
            <ng-container matColumnDef="title">
              <th mat-header-cell *matHeaderCellDef>Title</th>
              <td mat-cell *matCellDef="let book">{{ book.bookTitle }}</td>
            </ng-container>
            
            <!-- Author Column -->
            <ng-container matColumnDef="author">
              <th mat-header-cell *matHeaderCellDef>Author</th>
              <td mat-cell *matCellDef="let book">{{ book.author }}</td>
            </ng-container>
            
            <!-- Quantity Column -->
            <ng-container matColumnDef="quantity">
              <th mat-header-cell *matHeaderCellDef>Qty Sold</th>
              <td mat-cell *matCellDef="let book">{{ book.totalQuantitySold }}</td>
            </ng-container>
            
            <!-- Revenue Column -->
            <ng-container matColumnDef="revenue">
              <th mat-header-cell *matHeaderCellDef>Revenue</th>
              <td mat-cell *matCellDef="let book">{{ book.totalRevenue | currency }}</td>
            </ng-container>
            
            <tr mat-header-row *matHeaderRowDef="topBooksColumns"></tr>
            <tr mat-row *matRowDef="let row; columns: topBooksColumns;"></tr>
          </table>
          
          <div *ngIf="!metrics?.topSellingBooks?.length" class="no-data">
            No sales data available yet.
          </div>
        </mat-card-content>
      </mat-card>
      
      <!-- Connection Status -->
      <div class="connection-status">
        <mat-icon [class.connected]="wsConnected" [class.disconnected]="!wsConnected">
          {{ wsConnected ? 'wifi' : 'wifi_off' }}
        </mat-icon>
        <span>{{ wsConnected ? 'Real-time updates connected' : 'Connecting...' }}</span>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container {
      padding: 20px;
    }
    h1 {
      margin-bottom: 24px;
      color: #333;
    }
    .metrics-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
      margin-bottom: 24px;
    }
    .metric-card {
      text-align: center;
    }
    .metric-card mat-icon {
      font-size: 36px;
      width: 36px;
      height: 36px;
    }
    .metric-value {
      font-size: 32px;
      font-weight: bold;
      margin: 20px 0;
      color: #3f51b5;
    }
    .revenue .metric-value { color: #4caf50; }
    .sales .metric-value { color: #2196f3; }
    .books .metric-value { color: #ff9800; }
    .average .metric-value { color: #9c27b0; }
    
    .top-books-card {
      margin-top: 20px;
    }
    .top-books-table {
      width: 100%;
    }
    .no-data {
      text-align: center;
      padding: 40px;
      color: #999;
    }
    .loading {
      display: flex;
      justify-content: center;
      padding: 60px;
    }
    .connection-status {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-top: 20px;
      padding: 10px;
      background: #f5f5f5;
      border-radius: 4px;
    }
    .connected { color: #4caf50; }
    .disconnected { color: #f44336; }
  `]
})
export class AnalyticsDashboardComponent implements OnInit {
  // Data
  metrics: PerformanceMetrics | null = null;
  topBooksColumns: string[] = ['rank', 'title', 'author', 'quantity', 'revenue'];
  
  // State
  isLoading = true;
  wsConnected = false;
  
  // Computed
  get averageSaleValue(): number {
    if (!this.metrics || !this.metrics.totalSales) return 0;
    return this.metrics.totalRevenue / this.metrics.totalSales;
  }

  constructor(
    private apiService: ApiService,
    private wsService: WebSocketService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadMetrics();
    this.subscribeToWebSocket();
    this.subscribeToConnection();
  }

  /**
   * Load performance metrics from API
   */
  loadMetrics(): void {
    this.isLoading = true;
    this.apiService.getPerformanceSummary().subscribe({
      next: (metrics) => {
        this.metrics = metrics;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading metrics:', err);
        this.snackBar.open('Error loading analytics', 'Close', { duration: 3000 });
        this.isLoading = false;
      }
    });
  }

  /**
   * Subscribe to WebSocket for real-time alerts
   */
  subscribeToWebSocket(): void {
    this.wsService.lowStockAlerts$.subscribe((message) => {
      this.snackBar.open(message, 'View Inventory', { 
        duration: 8000 
      });
    });
  }

  /**
   * Subscribe to connection status
   */
  subscribeToConnection(): void {
    this.wsService.isConnected$.subscribe((connected) => {
      this.wsConnected = connected;
    });
  }

  /**
   * Refresh data
   */
  refreshData(): void {
    this.loadMetrics();
  }
}
