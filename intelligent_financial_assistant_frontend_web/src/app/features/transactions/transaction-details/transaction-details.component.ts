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
  transaction: Transaction | undefined;
  loading = true;
  error: string | null = null;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly transactionService: TransactionService
  ) { }

  ngOnInit(): void {
    const reference = this.route.snapshot.paramMap.get('reference');
    if (reference) {
      this.transactionService.getByReference(reference).subscribe({
        next: (data: Transaction) => {
          this.transaction = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Échec du chargement des détails de la transaction.';
          this.loading = false;
          console.error(err);
        }
      });
    }
  }
}
