import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { TransactionService } from '../../../services/transaction.service';
import { AccountService } from '../../../services/account.service';
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
    private accountService: AccountService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.transactionForm = this.fb.group({
      amount: ['', [Validators.required, Validators.min(0.01)]],
      sourceIban: ['', Validators.required],
      recipientIban: [''],
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
    const recipientControl = this.transactionForm.get('recipientIban');
    if (this.transactionType === 'transfer') {
      recipientControl?.setValidators([Validators.required]);
    } else {
      recipientControl?.clearValidators();
    }
    recipientControl?.updateValueAndValidity();
  }

  onSubmit(): void {
    if (this.transactionForm.invalid) {
      this.snackBar.open('Veuillez remplir tous les champs obligatoires.', 'Fermer', { duration: 3000 });
      return;
    }

    const formValue = this.transactionForm.value;
    const sourceIban = formValue.sourceIban;

    // 1. Resolve Account ID from IBAN
    this.accountService.getAccountByIban(sourceIban).subscribe({
      next: (account) => {
        if (!account || !account.id) {
          this.snackBar.open('Compte source introuvable.', 'Fermer', { duration: 3000 });
          return;
        }

        const accountId = account.id;
        let transactionObservable;

        // 2. Execute Transaction based on type
        switch (this.transactionType) {
          case 'deposit':
            transactionObservable = this.transactionService.createDeposit({
              bankAccountId: accountId,
              amount: formValue.amount,
              reason: formValue.reason
            });
            break;
          case 'withdrawal':
            transactionObservable = this.transactionService.createWithdrawal({
              bankAccountId: accountId,
              amount: formValue.amount,
              reason: formValue.reason
            });
            break;
          case 'transfer':
            transactionObservable = this.transactionService.createTransfer({
              bankAccountId: accountId,
              amount: formValue.amount,
              recipientIban: formValue.recipientIban,
              reason: formValue.reason
            });
            break;
        }

        if (transactionObservable) {
          transactionObservable.subscribe({
            next: () => {
              this.snackBar.open('Transaction réussie !', 'Fermer', { duration: 3000 });
              this.router.navigate(['/admin/transactions']);
            },
            error: (err) => {
              this.snackBar.open('Échec de la transaction. Vérifiez les données.', 'Fermer', { duration: 3000 });
              console.error(err);
            }
          });
        }
      },
      error: (err) => {
        this.snackBar.open('Impossible de trouver le compte source avec cet IBAN.', 'Fermer', { duration: 3000 });
        console.error('Account lookup error', err);
      }
    });
  }
}
//}
