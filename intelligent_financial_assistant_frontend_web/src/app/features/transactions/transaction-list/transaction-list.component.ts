import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { finalize, Subject, takeUntil } from 'rxjs';
import { TransactionService } from '../../../services/transaction.service';
import { Transaction } from '../../../models/transaction.model';

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
  styleUrls: ['./transaction-list.component.css'],
})
export class TransactionListComponent implements OnInit, AfterViewInit, OnDestroy {
  displayedColumns: string[] = ['amount', 'date', 'description','type', 'accountId', 'actions'];
  dataSource = new MatTableDataSource<Transaction>([]);
  filterForm!: FormGroup;
  isLoading = true;

  transactionTypes = ['DEBIT', 'CREDIT'];

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
    this.loadTransactions();

    this.filterForm.valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe(() => this.applyFilters());
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.dataSource.filterPredicate = this.createFilter();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm(): void {
    this.filterForm = this.fb.group({
      search: [''],
      type: [null],
      startDate: [null],
      endDate: [null]
    });
  }

  loadTransactions(): void {
    this.isLoading = true;
    this.transactionService.getTransactions().pipe(
      finalize(() => this.isLoading = false),
      takeUntil(this.destroy$)
    ).subscribe({
      next: (transactions) => {
        this.dataSource.data = transactions;
      },
      error: (err) => {
        console.error('Failed to load transactions', err);
        this.snackBar.open('Erreur lors du chargement des transactions.', 'Fermer', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  applyFilters(): void {
    const filterValue = this.filterForm.value;
    this.dataSource.filter = JSON.stringify(filterValue);

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  createFilter(): (data: Transaction, filter: string) => boolean {
    return (data: Transaction, filter: string): boolean => {
      const searchTerms = JSON.parse(filter);

      const date = new Date(data.date);
      const startDate = searchTerms.startDate ? new Date(searchTerms.startDate) : null;
      const endDate = searchTerms.endDate ? new Date(searchTerms.endDate) : null;

      if (startDate) startDate.setHours(0, 0, 0, 0);
      if (endDate) endDate.setHours(23, 59, 59, 999);

      const searchStr = (
        data.description +
        data.amount.toString() +
        data.accountId
      ).toLowerCase();

      return (
        searchStr.includes(searchTerms.search?.toLowerCase() ?? '') &&
        (searchTerms.type ? data.type === searchTerms.type : true) &&
        (startDate ? date >= startDate : true) &&
        (endDate ? date <= endDate : true)
      );
    };
  }

  deleteTransaction(id: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette transaction ?')) {
      this.transactionService.deleteTransaction(id).pipe(
        takeUntil(this.destroy$)
      ).subscribe({
        next: () => {
          this.snackBar.open('Transaction supprimée avec succès.', 'Fermer', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.loadTransactions();
        },
        error: (err) => {
          console.error('Failed to delete transaction', err);
          this.snackBar.open('Erreur lors de la suppression.', 'Fermer', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }

  clearFilters(): void {
    this.filterForm.reset({ search: '', type: null, startDate: null, endDate: null });
  }
}
