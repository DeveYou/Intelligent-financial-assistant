import { Component, OnInit } from '@angular/core';
import { CommonModule } from "@angular/common";
import { MatCardModule } from "@angular/material/card";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatTableModule } from "@angular/material/table";
import { MatChipsModule } from "@angular/material/chips";
import { MatMenuModule } from "@angular/material/menu";
import { UserService } from "../../services/user.service";
import { AccountService } from '../../services/account.service';
import { TransactionService } from '../../services/transaction.service';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

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

  constructor(
    private userService: UserService,
    private accountService: AccountService,
    private transactionService: TransactionService
  ) { }

  ngOnInit(): void {
    this.loadUserCount();
    this.loadAccountCount();
    this.loadTransactionStats();
    this.loadRecentTransactions();
    this.loadChartsData();
  }

  loadUserCount(): void {
    this.userService.getUserCount().subscribe({
      next: (count) => {
        this.stats[0].value = count.toString();
      },
      error: (err) => console.error('Error fetching user count', err)
    });
  }

  loadAccountCount(): void {
    this.accountService.getAccountCount().subscribe({
      next: (count) => {
        this.stats[1].value = count.toString();
      },
      error: (err) => console.error('Error fetching account count', err)
    });
  }

  loadTransactionStats(): void {
    this.transactionService.getTransactionStats().subscribe({
      next: (stats) => {
        // Update "Transactions (Aujourd'hui)"
        this.stats[2].value = (stats.todayTransactions || 0).toString();
        // Update "Volume Total"
        this.stats[3].value = (stats.totalVolume || 0).toLocaleString('fr-FR', { style: 'currency', currency: 'MAD' });
      },
      error: (err) => console.error('Error fetching transaction stats', err)
    });
  }

  loadRecentTransactions(): void {
    this.transactionService.getAllTransactions({ page: 0, size: 5, sortBy: 'date', sortDirection: 'DESC' }).subscribe({
      next: (response) => {
        const transactions = response.content || [];
        this.recentTransactions = transactions.map((t: any) => ({
          id: t.reference,
          user: `User ${t.userId}`,
          amount: t.amount,
          type: t.type,
          date: t.date,
          status: t.status
        }));
      },
      error: (err) => console.error('Error fetching recent transactions', err)
    });
  }

  loadChartsData(): void {
    // Account Distribution Chart
    this.accountService.getAccountDistribution().subscribe({
      next: (data) => {
        this.renderAccountChart(data);
      },
      error: (err) => console.error('Error fetching account distribution', err)
    });

    // Transaction Daily Chart
    this.transactionService.getDailyTransactionStats().subscribe({
      next: (data) => {
        this.renderTransactionChart(data);
      },
      error: (err) => console.error('Error fetching daily transaction stats', err)
    });
  }

  renderAccountChart(data: any[]): void {
    const ctx = document.getElementById('accountChart') as HTMLCanvasElement;
    if (!ctx) return;

    const labels = data.map(d => d.type);
    const counts = data.map(d => d.count);

    new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: labels,
        datasets: [{
          data: counts,
          backgroundColor: ['#3f51b5', '#ff4081', '#ff9800'],
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { position: 'bottom' }
        }
      }
    });
  }

  renderTransactionChart(data: any[]): void {
    const ctx = document.getElementById('transactionChart') as HTMLCanvasElement;
    if (!ctx) return;

    // Sort by date just in case
    data.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());

    const labels = data.map(d => new Date(d.date).toLocaleDateString());
    const counts = data.map(d => d.count);

    new Chart(ctx, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [{
          label: 'Transactions',
          data: counts,
          borderColor: '#3f51b5',
          tension: 0.4,
          fill: true,
          backgroundColor: 'rgba(63, 81, 181, 0.2)'
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: false }
        },
        scales: {
          y: { BEGIN_AT_ZERO: true, ticks: { precision: 0 } }
        }
      } as any
    });
  }

  viewDetails(transaction: RecentTransaction): void {
    console.log('View details:', transaction);
  }

}
