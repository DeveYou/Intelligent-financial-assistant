import {Routes} from '@angular/router';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: 'admin',
    loadChildren: () => import('./layout/admin-layout/admin-layout.routes').then(m => m.ADMIN_ROUTES)
  },
  {
    path: 'transactions',
    loadChildren: () => import('./features/transactions/transactions.routes').then(m => m.TRANSACTION_ROUTES)
  },
  {
    path: 'access-denied',
    loadComponent: () => import('./features/errors/access-denied/access-denied.component').then(m => m.AccessDeniedComponent)
  },
  {
    path: '',
    redirectTo: '/auth/login',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: '/auth/login'
  }
];
