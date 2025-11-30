import { Routes } from '@angular/router';
import { AdminLayoutComponent } from './admin-layout.component';
import {authGuard} from "../../core/auth";

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('../../features/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'users',
        loadComponent: () => import('../../features/users/user-list/user-list.component').then(m => m.UserListComponent)
      },
      {
        path: 'accounts',
        loadComponent: () => import('../../features/accounts/account-list/account-list.component').then(m => m.AccountListComponent)
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  }
];
