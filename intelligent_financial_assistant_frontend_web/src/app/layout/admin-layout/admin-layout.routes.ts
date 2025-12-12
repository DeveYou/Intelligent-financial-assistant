import { Routes } from '@angular/router';
import { AdminLayoutComponent } from './admin-layout.component';
import {authGuard} from "../../core/auth";

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    canActivate: [authGuard],
    data: { roles: ['ROLE_ADMIN'] },
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
        path: 'users/:id',
        loadComponent: () => import('../../features/users/user-details/user-details.component').then(m => m.UserDetailsComponent)
      },
      {
        path: 'accounts',
        loadComponent: () => import('../../features/accounts/account-list/account-list.component').then(m => m.AccountListComponent)
      },
      {
        path: 'profile',
        loadComponent: () => import('../../features/profile/profile.component').then(m => m.ProfileComponent)
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  }
];
