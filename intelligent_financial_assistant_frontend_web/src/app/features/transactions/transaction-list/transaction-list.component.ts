import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { TransactionService } from '../../../services/transaction.service';
import { Transaction } from '../../../models/transaction.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatMenuModule } from '@angular/material/menu';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

@Component({
  selector: 'app-transaction-list',
  templateUrl: './transaction-list.component.html',
  styleUrls: ['./transaction-list.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatMenuModule,
    MatDatepickerModule,
    MatNativeDateModule
  ]
})
export class TransactionListComponent implements OnInit, AfterViewInit {
  dataSource: MatTableDataSource<Transaction> = new MatTableDataSource<Transaction>();
  displayedColumns: string[] = ['reference', 'type', 'status', 'amount', 'date', 'bankAccountId', 'recipientId', 'actions'];
  loading = true;
  error: string | null = null;
  totalTransactions = 0;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  // Paramètres de recherche étendus
  searchParams = {
    userId: '',
    bankAccountId: '',
    type: '',
    status: '',
    reference: '',
    startDate: '',
    endDate: '',
    page: 0,
    size: 10,
    sortBy: 'date',
    sortDirection: 'DESC'
  };

  // Options pour les dropdowns
  transactionTypes = [
    { value: '', label: 'Tous les types' },
    { value: 'DEPOSIT', label: 'Dépôt' },
    { value: 'WITHDRAWAL', label: 'Retrait' },
    { value: 'TRANSFER', label: 'Transfert' }
  ];

  transactionStatuses = [
    { value: '', label: 'Tous les statuts' },
    { value: 'PENDING', label: 'En attente' },
    { value: 'COMPLETED', label: 'Complétée' },
    { value: 'FAILED', label: 'Échouée' },
    { value: 'CANCELLED', label: 'Annulée' }
  ];

  constructor(
    private readonly transactionService: TransactionService, 
    private readonly router: Router
  ) { }

  ngOnInit(): void {
    this.loadTransactions();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    
    // Écouter les événements de pagination
    this.paginator.page.subscribe(() => {
      this.searchParams.page = this.paginator.pageIndex;
      this.searchParams.size = this.paginator.pageSize;
      this.loadTransactions();
    });
  }

  loadTransactions(): void {
    this.loading = true;
    this.error = null;
    
    this.transactionService.getAllTransactions(this.searchParams).subscribe({
      next: (response: any) => {
        if (response && response.content) {
          this.dataSource.data = response.content;
          this.totalTransactions = response.totalElements || response.content.length;
          
          // Mettre à jour le paginator si nécessaire
          if (this.paginator) {
            this.paginator.length = this.totalTransactions;
            this.paginator.pageIndex = this.searchParams.page;
          }
        } else {
          // Si la réponse est un simple tableau
          this.dataSource.data = response;
          this.totalTransactions = response.length;
        }
        
        this.dataSource.sort = this.sort;
        this.loading = false;
      },
      error: err => {
        this.error = 'Échec du chargement des transactions.';
        this.loading = false;
        console.error('Erreur:', err);
      }
    });
  }

  onSearch(): void {
    // Réinitialiser à la première page lors d'une nouvelle recherche
    this.searchParams.page = 0;
    if (this.paginator) {
      this.paginator.pageIndex = 0;
    }
    this.loadTransactions();
  }

  onReset(): void {
    this.searchParams = {
      userId: '',
      bankAccountId: '',
      type: '',
      status: '',
      reference: '',
      startDate: '',
      endDate: '',
      page: 0,
      size: 10,
      sortBy: 'date',
      sortDirection: 'DESC'
    };
    
    if (this.paginator) {
      this.paginator.pageIndex = 0;
    }
    
    this.loadTransactions();
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  viewDetails(reference: string): void {
    this.router.navigate(['/admin/transactions', reference]);
  }

  newTransaction(type: 'deposit' | 'withdrawal' | 'transfer'): void {
    this.router.navigate(['/admin/transactions/new', { type }]);
  }

  // Formater la date pour l'affichage
  formatDate(dateString: string): string {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  // Récupérer le libellé du type
  getTypeLabel(type: string): string {
    const found = this.transactionTypes.find(t => t.value === type);
    return found ? found.label : type;
  }

  // Récupérer le libellé du statut
  getStatusLabel(status: string): string {
    const found = this.transactionStatuses.find(s => s.value === status);
    return found ? found.label : status;
  }
}