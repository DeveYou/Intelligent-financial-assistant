import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { TransactionService } from '../../../services/transaction.service';
import { Transaction } from '../../../models/transaction.model';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-transaction-details',
  templateUrl: './transaction-details.component.html',
  styleUrls: ['./transaction-details.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule
  ]
})
export class TransactionDetailsComponent implements OnInit {
  transaction: Transaction | null = null;
  loading = true;
  error: string | null = null;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly transactionService: TransactionService
  ) { }

  ngOnInit(): void {
    const reference = this.route.snapshot.paramMap.get('reference');
    if (reference) {
      this.loadTransactionByReference(reference);
    } else {
      this.error = 'Référence de transaction non fournie.';
      this.loading = false;
    }
  }

  private loadTransactionByReference(reference: string): void {
    this.loading = true;
    this.error = null;
    
    this.transactionService.getByReference(reference).subscribe({
      next: (data: Transaction) => {
        this.transaction = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Échec du chargement des détails de la transaction.';
        this.loading = false;
        console.error('Erreur:', err);
      }
    });
  }

  getTypeLabel(type?: string): string {
    if (!type) return 'Inconnu';
    switch(type) {
      case 'DEPOSIT': return 'Dépôt';
      case 'WITHDRAWAL': return 'Retrait';
      case 'TRANSFER': return 'Transfert';
      default: return type;
    }
  }

  getStatusLabel(status?: string): string {
    if (!status) return 'Inconnu';
    switch(status) {
      case 'PENDING': return 'En attente';
      case 'COMPLETED': return 'Complétée';
      case 'FAILED': return 'Échouée';
      case 'CANCELLED': return 'Annulée';
      default: return status;
    }
  }

  formatDate(dateString?: string): string {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  }

  printDetails(): void {
    window.print();
  }

  cancelTransaction(): void {
   
      alert('Transaction annulée avec succès.'); 
      }

}