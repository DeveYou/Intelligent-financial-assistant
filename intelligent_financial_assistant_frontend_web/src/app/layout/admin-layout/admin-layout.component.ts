import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {AuthService} from "../../core/auth/auth.service";
import {CommonModule} from "@angular/common";
import {RouterModule} from "@angular/router";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatMenuModule} from "@angular/material/menu";
import {MatBadgeModule} from "@angular/material/badge";
import {MatDividerModule} from "@angular/material/divider";
import {MatListModule} from "@angular/material/list";

interface MenuItem {
  icon: string;
  label: string;
  route: string;
}

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatBadgeModule,
    MatDividerModule
  ],
  templateUrl: './admin-layout.component.html',
  styleUrl: './admin-layout.component.css'
})
export class AdminLayoutComponent {

  isSidebarOpen = true;
  userName = 'Admin User';

  menuItems: MenuItem[] = [
    {icon: 'dashboard', label: 'Dashboard', route: '/admin/dashboard'},
    {icon: 'people', label: 'Utilisateurs', route: '/admin/users'},
    {icon: 'account_balance', label: 'Comptes Bancaires', route: '/admin/accounts'},
    {icon: 'receipt_long', label: 'Transactions', route: '/admin/transactions'},
  ];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    // Récupérer les informations de l'utilisateur connecté
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.userName = `${currentUser.firstName} ${currentUser.lastName}`;
    }
  }

  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/auth/login']),
      error: () => this.router.navigate(['/auth/login'])
    });
  }

}
