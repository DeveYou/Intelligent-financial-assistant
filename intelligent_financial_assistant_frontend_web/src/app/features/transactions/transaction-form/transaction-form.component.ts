import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { TransactionService } from '../../../services/transaction.service';
import { AuthService } from '../../../core/auth/auth.service';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-transaction-form',
  templateUrl: './transaction-form.component.html',
  styleUrls: ['./transaction-form.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule
  ]
})
export class TransactionFormComponent implements OnInit {
  transactionForm: FormGroup;
  transactionType: 'deposit' | 'withdrawal' | 'transfer' = 'deposit';
  pageTitle = 'Nouvelle Transaction';

  constructor(
    private fb: FormBuilder,
    private transactionService: TransactionService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.transactionForm = this.fb.group({
      amount: ['', [Validators.required, Validators.min(0.01)]],
      bankAccountId: ['', Validators.required],
      destinationBankAccountId: [''],
      reason: ['']
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const type = params.get('type');
      if (type) {
        this.transactionType = type as 'deposit' | 'withdrawal' | 'transfer';
        this.pageTitle = `Nouveau ${this.transactionType.charAt(0).toUpperCase() + this.transactionType.slice(1)}`;
        this.updateFormValidators();
      }
    });
  }

  updateFormValidators(): void {
    const destAccountControl = this.transactionForm.get('destinationBankAccountId');
    if (this.transactionType === 'transfer') {
      destAccountControl?.setValidators([Validators.required]);
    } else {
      destAccountControl?.clearValidators();
    }
    destAccountControl?.updateValueAndValidity();
  }

  onSubmit(): void {
    if (this.transactionForm.invalid) {
      this.snackBar.open('Veuillez remplir tous les champs obligatoires.', 'Fermer', { duration: 3000 });
      return;
    }

    const formValue = this.transactionForm.value;
    let transactionObservable;

    switch (this.transactionType) {
      case 'deposit':
        transactionObservable = this.transactionService.deposit({ bankAccountId: formValue.bankAccountId, amount: formValue.amount, reason: formValue.reason });
        break;
      case 'withdrawal':
        transactionObservable = this.transactionService.withdraw({ bankAccountId: formValue.bankAccountId, amount: formValue.amount, reason: formValue.reason });
        break;
      case 'transfer':
        transactionObservable = this.transactionService.transfer({ sourceBankAccountId: formValue.bankAccountId, destinationBankAccountId: formValue.destinationBankAccountId, amount: formValue.amount, reason: formValue.reason });
        break;
    }

    transactionObservable.subscribe({
      next: () => {
        this.snackBar.open('Transaction réussie !', 'Fermer', { duration: 3000 });
        this.router.navigate(['/admin/transactions']);
      },
      error: (err) => {
        this.snackBar.open('Échec de la transaction.', 'Fermer', { duration: 3000, panelClass: ['error-snackbar'] });
        console.error(err);
      }
    });
  }
}
