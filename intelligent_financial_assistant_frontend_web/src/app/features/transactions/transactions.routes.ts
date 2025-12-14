import { Routes } from '@angular/router';
import { TransactionListComponent } from './transaction-list/transaction-list.component';
import { TransactionFormComponent } from './transaction-form/transaction-form.component';
import { TransactionDetailsComponent } from './transaction-details/transaction-details.component';

export const TRANSACTION_ROUTES: Routes = [
  { path: '', component: TransactionListComponent },
  { path: 'new', component: TransactionFormComponent },
  { path: ':reference', component: TransactionDetailsComponent }
];

