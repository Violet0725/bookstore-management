import { Component, OnInit, ViewChild, AfterViewInit, TemplateRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ApiService } from '../../services/api.service';
import { WebSocketService } from '../../services/websocket.service';
import { Book, BookCreate } from '../../models/book.model';
import { SaleCreate } from '../../models/sale.model';

/**
 * InventoryComponent - Book Management Dashboard
 * 
 * Displays books in a Material table with CRUD operations.
 * Also handles recording sales and displays real-time low stock alerts.
 * 
 * Interview Points:
 * - Angular Material table with pagination and sorting
 * - Reactive Forms for form validation
 * - MatDialog for modal dialogs
 * - RxJS subscription management
 * - WebSocket integration for real-time updates
 */
@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatSnackBarModule,
    MatFormFieldModule,
    MatInputModule,
    MatCardModule,
    MatTooltipModule,
    MatProgressSpinnerModule
  ],
  template: `
    <div class="inventory-container">
      <mat-card>
        <mat-card-header>
          <mat-card-title>Book Inventory</mat-card-title>
          <span class="spacer"></span>
          <button mat-raised-button color="primary" (click)="openAddDialog()">
            <mat-icon>add</mat-icon> Add Book
          </button>
        </mat-card-header>
        
        <mat-card-content>
          <!-- Loading indicator -->
          <div *ngIf="isLoading" class="loading">
            <mat-spinner diameter="40"></mat-spinner>
          </div>
          
          <!-- Books Table -->
          <table mat-table [dataSource]="dataSource" matSort *ngIf="!isLoading">
            <!-- ID Column -->
            <ng-container matColumnDef="id">
              <th mat-header-cell *matHeaderCellDef mat-sort-header>ID</th>
              <td mat-cell *matCellDef="let book">{{ book.id }}</td>
            </ng-container>
            
            <!-- Title Column -->
            <ng-container matColumnDef="title">
              <th mat-header-cell *matHeaderCellDef mat-sort-header>Title</th>
              <td mat-cell *matCellDef="let book">{{ book.title }}</td>
            </ng-container>
            
            <!-- Author Column -->
            <ng-container matColumnDef="author">
              <th mat-header-cell *matHeaderCellDef mat-sort-header>Author</th>
              <td mat-cell *matCellDef="let book">{{ book.author }}</td>
            </ng-container>
            
            <!-- ISBN Column -->
            <ng-container matColumnDef="isbn">
              <th mat-header-cell *matHeaderCellDef mat-sort-header>ISBN</th>
              <td mat-cell *matCellDef="let book">{{ book.isbn }}</td>
            </ng-container>
            
            <!-- Price Column -->
            <ng-container matColumnDef="price">
              <th mat-header-cell *matHeaderCellDef mat-sort-header>Price</th>
              <td mat-cell *matCellDef="let book">{{ book.price | currency }}</td>
            </ng-container>
            
            <!-- Stock Column -->
            <ng-container matColumnDef="stockQuantity">
              <th mat-header-cell *matHeaderCellDef mat-sort-header>Stock</th>
              <td mat-cell *matCellDef="let book">
                <span [class.low-stock]="book.stockQuantity < 5">
                  {{ book.stockQuantity }}
                </span>
              </td>
            </ng-container>
            
            <!-- Actions Column -->
            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef>Actions</th>
              <td mat-cell *matCellDef="let book">
                <button mat-icon-button color="primary" (click)="openEditDialog(book)" matTooltip="Edit">
                  <mat-icon>edit</mat-icon>
                </button>
                <button mat-icon-button color="accent" (click)="openSaleDialog(book)" matTooltip="Record Sale">
                  <mat-icon>shopping_cart</mat-icon>
                </button>
                <button mat-icon-button color="warn" (click)="deleteBook(book)" matTooltip="Delete">
                  <mat-icon>delete</mat-icon>
                </button>
              </td>
            </ng-container>
            
            <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
            <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
          </table>
          
          <mat-paginator [pageSizeOptions]="[5, 10, 20]" showFirstLastButtons></mat-paginator>
        </mat-card-content>
      </mat-card>
    </div>
    
    <!-- Add/Edit Dialog Template -->
    <ng-template #bookDialog let-data>
      <h2 mat-dialog-title>{{ data.isEdit ? 'Edit Book' : 'Add New Book' }}</h2>
      <mat-dialog-content>
        <form [formGroup]="bookForm">
          <mat-form-field appearance="fill">
            <mat-label>Title</mat-label>
            <input matInput formControlName="title" required>
          </mat-form-field>
          
          <mat-form-field appearance="fill">
            <mat-label>Author</mat-label>
            <input matInput formControlName="author" required>
          </mat-form-field>
          
          <mat-form-field appearance="fill">
            <mat-label>ISBN</mat-label>
            <input matInput formControlName="isbn">
          </mat-form-field>
          
          <mat-form-field appearance="fill">
            <mat-label>Price</mat-label>
            <input matInput type="number" formControlName="price" required>
            <span matPrefix>$&nbsp;</span>
          </mat-form-field>
          
          <mat-form-field appearance="fill">
            <mat-label>Stock Quantity</mat-label>
            <input matInput type="number" formControlName="stockQuantity" required>
          </mat-form-field>
        </form>
      </mat-dialog-content>
      <mat-dialog-actions align="end">
        <button mat-button mat-dialog-close>Cancel</button>
        <button mat-raised-button color="primary" (click)="saveBook(data.isEdit)"
                [disabled]="!bookForm.valid">
          {{ data.isEdit ? 'Update' : 'Add' }}
        </button>
      </mat-dialog-actions>
    </ng-template>
    
    <!-- Sale Dialog Template -->
    <ng-template #saleDialog let-data>
      <h2 mat-dialog-title>Record Sale</h2>
      <mat-dialog-content>
        <p><strong>Book:</strong> {{ data.book.title }}</p>
        <p><strong>Current Stock:</strong> {{ data.book.stockQuantity }}</p>
        <p><strong>Price:</strong> {{ data.book.price | currency }}</p>
        
        <form [formGroup]="saleForm">
          <mat-form-field appearance="fill">
            <mat-label>Quantity</mat-label>
            <input matInput type="number" formControlName="quantitySold" required min="1" [max]="data.book.stockQuantity">
          </mat-form-field>
          
          <p *ngIf="saleForm.get('quantitySold')?.value">
            <strong>Total:</strong> {{ data.book.price * saleForm.get('quantitySold')?.value | currency }}
          </p>
        </form>
      </mat-dialog-content>
      <mat-dialog-actions align="end">
        <button mat-button mat-dialog-close>Cancel</button>
        <button mat-raised-button color="accent" (click)="recordSale(data.book)"
                [disabled]="!saleForm.valid">
          Record Sale
        </button>
      </mat-dialog-actions>
    </ng-template>
  `,
  styles: [`
    .inventory-container {
      padding: 20px;
    }
    mat-card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
    }
    table {
      width: 100%;
    }
    .spacer {
      flex: 1 1 auto;
    }
    .loading {
      display: flex;
      justify-content: center;
      padding: 40px;
    }
    .low-stock {
      color: #f44336;
      font-weight: bold;
    }
    mat-form-field {
      width: 100%;
      margin-bottom: 8px;
    }
  `]
})
export class InventoryComponent implements OnInit, AfterViewInit {
  // Table configuration
  displayedColumns: string[] = ['id', 'title', 'author', 'isbn', 'price', 'stockQuantity', 'actions'];
  dataSource: MatTableDataSource<Book>;
  
  // Forms
  bookForm: FormGroup;
  saleForm: FormGroup;
  
  // State
  isLoading = true;
  currentBook: Book | null = null;
  
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('bookDialog') bookDialogTemplate!: TemplateRef<any>;
  @ViewChild('saleDialog') saleDialogTemplate!: TemplateRef<any>;

  constructor(
    private apiService: ApiService,
    private wsService: WebSocketService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private fb: FormBuilder
  ) {
    // Initialize data source
    this.dataSource = new MatTableDataSource<Book>();
    
    // Initialize forms
    this.bookForm = this.fb.group({
      title: ['', Validators.required],
      author: ['', Validators.required],
      isbn: [''],
      price: ['', [Validators.required, Validators.min(0)]],
      stockQuantity: ['', [Validators.required, Validators.min(0)]]
    });
    
    this.saleForm = this.fb.group({
      quantitySold: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.loadBooks();
    this.subscribeToWebSocket();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  /**
   * Load books from API
   */
  loadBooks(): void {
    this.isLoading = true;
    this.apiService.getAllBooks().subscribe({
      next: (books) => {
        this.dataSource.data = books;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading books:', err);
        this.snackBar.open('Error loading books', 'Close', { duration: 3000 });
        this.isLoading = false;
      }
    });
  }

  /**
   * Subscribe to WebSocket for real-time updates
   */
  subscribeToWebSocket(): void {
    // Subscribe to low stock alerts
    this.wsService.lowStockAlerts$.subscribe((message) => {
      this.snackBar.open(message, 'View', { duration: 5000 })
        .onAction()
        .subscribe(() => {
          // Refresh books when notification is clicked
          this.loadBooks();
        });
    });
  }

  /**
   * Open dialog to add new book
   */
  openAddDialog(): void {
    this.currentBook = null;
    this.bookForm.reset();
    this.showBookDialog(false);
  }

  /**
   * Open dialog to edit existing book
   */
  openEditDialog(book: Book): void {
    this.currentBook = book;
    this.bookForm.patchValue(book);
    this.showBookDialog(true);
  }

  /**
   * Show book dialog (add/edit)
   */
  private showBookDialog(isEdit: boolean): void {
    this.dialog.open(this.bookDialogTemplate, {
      width: '500px',
      disableClose: false,
      data: { isEdit }
    });
  }

  /**
   * Save book (create or update)
   */
  saveBook(isEdit: boolean): void {
    if (this.bookForm.invalid) return;
    
    const bookData: BookCreate = this.bookForm.value;
    
    if (isEdit && this.currentBook) {
      // Update existing book
      this.apiService.updateBook(this.currentBook.id!, bookData).subscribe({
        next: () => {
          this.snackBar.open('Book updated successfully', 'Close', { duration: 3000 });
          this.loadBooks();
          this.bookForm.reset();
        },
        error: (err) => {
          this.snackBar.open('Error updating book', 'Close', { duration: 3000 });
        }
      });
    } else {
      // Create new book
      this.apiService.createBook(bookData).subscribe({
        next: () => {
          this.snackBar.open('Book created successfully', 'Close', { duration: 3000 });
          this.loadBooks();
          this.bookForm.reset();
        },
        error: (err) => {
          this.snackBar.open('Error creating book', 'Close', { duration: 3000 });
        }
      });
    }
  }

  /**
   * Delete a book
   */
  deleteBook(book: Book): void {
    if (confirm(`Are you sure you want to delete "${book.title}"?`)) {
      this.apiService.deleteBook(book.id!).subscribe({
        next: () => {
          this.snackBar.open('Book deleted successfully', 'Close', { duration: 3000 });
          this.loadBooks();
        },
        error: (err) => {
          this.snackBar.open('Error deleting book', 'Close', { duration: 3000 });
        }
      });
    }
  }

  /**
   * Open dialog to record a sale
   */
  openSaleDialog(book: Book): void {
    this.currentBook = book;
    this.saleForm.reset();
    
    this.dialog.open(this.saleDialogTemplate, {
      width: '400px',
      disableClose: false,
      data: { book }
    });
  }
      }
    }
  }

  /**
   * Record sale for a book
   */
  recordSale(book: Book, quantity?: number): void {
    if (!quantity) {
      const qtyStr = prompt(`Enter quantity to sell (max ${book.stockQuantity}):`);
      quantity = parseInt(qtyStr || '0', 10);
    }
    
    if (quantity > 0 && quantity <= book.stockQuantity) {
      this.recordSaleDirect(book, quantity);
    } else {
      this.snackBar.open('Invalid quantity', 'Close', { duration: 3000 });
    }
  }

  /**
   * Record sale directly
   */
  private recordSaleDirect(book: Book, quantity: number): void {
    const sale: SaleCreate = {
      bookId: book.id!,
      quantitySold: quantity
    };
    
    this.apiService.recordSale(sale).subscribe({
      next: (result) => {
        this.snackBar.open(
          `Sale recorded! Total: ${result.totalAmount}`, 
          'Close', 
          { duration: 3000 }
        );
        this.loadBooks();  // Refresh to show updated stock
      },
      error: (err) => {
        this.snackBar.open('Error recording sale: ' + err.error?.message || 'Unknown error', 'Close', { duration: 3000 });
      }
    });
  }
}
