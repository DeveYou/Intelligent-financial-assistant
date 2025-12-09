import {Routes} from '@angular/router';
import {authGuard} from "../../core/auth";

export const TRANSACTION_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./transaction-list/transaction-list.component').then(m => m.TransactionListComponent),
    canActivate: [authGuard]
  }
];

