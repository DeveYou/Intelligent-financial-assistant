import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {MatCardModule} from '@angular/material/card';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {MatSort, MatSortModule} from '@angular/material/sort';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatChipsModule} from '@angular/material/chips';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatMenuModule} from '@angular/material/menu';
import {MatDividerModule} from '@angular/material/divider';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {finalize, Subject, takeUntil} from 'rxjs';
import {TransactionService} from '../../../services/transaction.service';
import { Transaction as TransactionModel, TransactionType, TransactionStatus } from '../../../models/transaction.model';

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    DatePipe,
    MatCardModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatChipsModule,
    MatTooltipModule,
    MatMenuModule,
    MatDividerModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './transaction-list.component.html',
  styleUrls: ['./transaction-list.component.css']
})
export class TransactionListComponent implements OnInit, AfterViewInit, OnDestroy {
  displayedColumns: string[] = ['reference', 'account', 'type', 'amount', 'receiver', 'date', 'status', 'actions'];
  dataSource = new MatTableDataSource<TransactionModel>([]);
  filterForm!: FormGroup;
  isLoading = false;

  // Exposer les enums au template
  public TransactionType = TransactionType;
  public TransactionStatus = TransactionStatus;

  // Listes filtrées [key, value] pour ngFor
  public transactionTypeEntries: [string, string][] = [];
  public transactionStatusEntries: [string, string][] = [];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly fb: FormBuilder,
    private readonly transactionService: TransactionService,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initForm();

    // Construire les entries depuis les enums en filtrant les clés numériques
    this.transactionTypeEntries = Object.entries(this.TransactionType).filter(([k]) => isNaN(Number(k))) as [string, string][];
    this.transactionStatusEntries = Object.entries(this.TransactionStatus).filter(([k]) => isNaN(Number(k))) as [string, string][];

    this.loadTransactions();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm(): void {
    this.filterForm = this.fb.group({
      search: [''],
      type: [''],
      status: [''],
      startDate: [''],
      endDate: ['']
    });
  }

  loadTransactions(): void {
    if (!this.filterForm) {
      return;
    }

    this.isLoading = true;
    this.transactionService
      .getTransactions()
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => (this.isLoading = false))
      )
      .subscribe({
        next: (transactions: TransactionModel[]) => {
          this.dataSource.data = transactions ?? [];
        },
        error: () => {
          this.snackBar.open('Impossible de charger les transactions', 'Fermer', {duration: 3000});
        }
      });
  }

  applyFilter(): void {
    // filtrage côté client simple
    const { search, type, status, startDate, endDate } = this.filterForm.value;
    let filtered = this.dataSource.data.slice();

    if (search) {
      const s = (search as string).toLowerCase();
      filtered = filtered.filter(t =>
        (t.reference || '').toLowerCase().includes(s) ||
        (t.bankAccountId || '').toLowerCase().includes(s) ||
        (t.receiver || '').toLowerCase().includes(s)
      );
    }

    if (type) {
      filtered = filtered.filter(t => t.type === type);
    }

    if (status) {
      filtered = filtered.filter(t => t.status === status);
    }

    if (startDate) {
      const sd = new Date(startDate);
      filtered = filtered.filter(t => new Date(t.date) >= sd);
    }

    if (endDate) {
      const ed = new Date(endDate);
      filtered = filtered.filter(t => new Date(t.date) <= ed);
    }

    this.dataSource.data = filtered;
  }

  clearFilters(): void {
    this.filterForm.reset({
      search: '',
      type: '',
      status: '',
      startDate: '',
      endDate: ''
    });
    this.loadTransactions();
  }

  refreshRow(transaction: TransactionModel): void {
    this.transactionService
      .getTransactions()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updated: TransactionModel[]) => {
          const idx = this.dataSource.data.findIndex(t => t.id === transaction.id);
          if (idx !== -1 && updated.length > 0) {
            // remplacer la ligne avec la transaction correspondante si trouvée
            const updatedTx = updated.find(u => u.id === transaction.id);
            if (updatedTx) {
              const updatedData = [...this.dataSource.data];
              updatedData[idx] = updatedTx;
              this.dataSource.data = updatedData;
            }
          }
        },
        error: () => {
          this.snackBar.open('Impossible de rafraîchir la transaction', 'Fermer', {duration: 3000});
        }
      });
  }

  downloadReceipt(transaction: TransactionModel): void {
    console.log('Download receipt for', transaction.reference);
    this.snackBar.open('Téléchargement du reçu non encore implémenté', 'Fermer', {duration: 3000});
  }

  createTransaction(transaction: TransactionModel): void {
    this.transactionService.createTransaction(transaction).subscribe({
      next: (newTransaction) => {
        this.dataSource.data = [...this.dataSource.data, newTransaction];
        this.snackBar.open('Transaction créée avec succès', 'Fermer', { duration: 3000 });
      },
      error: () => {
        this.snackBar.open('Erreur lors de la création de la transaction', 'Fermer', { duration: 3000 });
      }
    });
  }

  updateTransaction(transaction: TransactionModel): void {
    this.transactionService.updateTransaction(transaction.id, transaction).subscribe({
      next: (updatedTransaction) => {
        const index = this.dataSource.data.findIndex(t => t.id === transaction.id);
        if (index !== -1) {
          this.dataSource.data[index] = updatedTransaction;
          this.dataSource._updateChangeSubscription();
        }
        this.snackBar.open('Transaction mise à jour avec succès', 'Fermer', { duration: 3000 });
      },
      error: () => {
        this.snackBar.open('Erreur lors de la mise à jour de la transaction', 'Fermer', { duration: 3000 });
      }
    });
  }

  deleteTransaction(id: string): void {
    this.transactionService.deleteTransaction(id).subscribe({
      next: () => {
        this.dataSource.data = this.dataSource.data.filter(t => t.id !== id);
        this.snackBar.open('Transaction supprimée avec succès', 'Fermer', { duration: 3000 });
      },
      error: () => {
        this.snackBar.open('Erreur lors de la suppression de la transaction', 'Fermer', { duration: 3000 });
      }
    });
  }
}
