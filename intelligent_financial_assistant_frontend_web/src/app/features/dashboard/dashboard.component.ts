import {Component, OnInit} from '@angular/core';
import {CommonModule} from "@angular/common";
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatTableModule} from "@angular/material/table";
import {MatChipsModule} from "@angular/material/chips";
import {MatMenuModule} from "@angular/material/menu";
import {UserService} from "../../services/user.service";

interface StatCard {
  title: string;
  value: string;
  icon: string;
  color: string;
  trend: string;
  trendUp: boolean;
}

interface RecentTransaction {
  id: string;
  user: string;
  amount: number;
  type: string;
  date: Date;
  status: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatTableModule,
    MatChipsModule,
    MatMenuModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  stats: StatCard[] = [
    {
      title: 'Utilisateurs Actifs',
      value: '1,234',
      icon: 'people',
      color: '#4caf50',
      trend: '+12%',
      trendUp: true
    },
    {
      title: 'Comptes Bancaires',
      value: '856',
      icon: 'account_balance',
      color: '#2196f3',
      trend: '+8%',
      trendUp: true
    },
    {
      title: 'Transactions (Aujourd\'hui)',
      value: '3,456',
      icon: 'receipt_long',
      color: '#ff9800',
      trend: '+15%',
      trendUp: true
    },
    {
      title: 'Volume Total',
      value: '€2.4M',
      icon: 'trending_up',
      color: '#9c27b0',
      trend: '-3%',
      trendUp: false
    }
  ];

  recentTransactions: RecentTransaction[] = [
    {
      id: 'TXN001',
      user: 'Jean Dupont',
      amount: 1500.00,
      type: 'Dépôt',
      date: new Date(),
      status: 'Complété'
    },
    {
      id: 'TXN002',
      user: 'Marie Martin',
      amount: -850.50,
      type: 'Retrait',
      date: new Date(),
      status: 'Complété'
    },
    {
      id: 'TXN003',
      user: 'Pierre Bernard',
      amount: 2300.00,
      type: 'Virement',
      date: new Date(),
      status: 'En attente'
    }
  ];

  displayedColumns: string[] = ['id', 'user', 'type', 'amount', 'date', 'status', 'actions'];

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUserCount();
  }

  loadUserCount(): void {
    this.userService.getUserCount().subscribe({
      next: (count) => {
        // Update the first card (Utilisateurs Actifs)
        this.stats[0].value = count.toString();
      },
      error: (err) => {
        console.error('Error fetching user count', err);
      }
    });
  }

  viewDetails(transaction: RecentTransaction): void {
    console.log('View details:', transaction);
  }

}
