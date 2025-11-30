// src/app/features/accounts/account-list/account-list.component.ts
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import {MatDivider} from "@angular/material/divider";

interface BankAccount {
  id: string;
  accountNumber: string;
  owner: string;
  type: string;
  balance: number;
  status: string;
  createdAt: Date;
  lastTransaction: Date;
}

@Component({
  selector: 'app-account-list',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatChipsModule,
    MatMenuModule,
    MatTooltipModule,
    MatDivider
  ],
  templateUrl: './account-list.component.html',
  styleUrls: ['./account-list.component.css']
})
export class AccountListComponent implements OnInit {
  displayedColumns: string[] = ['accountNumber', 'owner', 'type', 'balance', 'lastTransaction', 'status', 'actions'];
  dataSource!: MatTableDataSource<BankAccount>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  accounts: BankAccount[] = [
    {
      id: 'ACC001',
      accountNumber: 'FR76 1234 5678 9012 3456 7890 123',
      owner: 'Jean Dupont',
      type: 'Compte Courant',
      balance: 15250.00,
      status: 'Actif',
      createdAt: new Date('2024-01-15'),
      lastTransaction: new Date('2024-11-28')
    },
    {
      id: 'ACC002',
      accountNumber: 'FR76 9876 5432 1098 7654 3210 987',
      owner: 'Marie Martin',
      type: 'Compte Épargne',
      balance: 8430.50,
      status: 'Actif',
      createdAt: new Date('2024-02-20'),
      lastTransaction: new Date('2024-11-25')
    },
    {
      id: 'ACC003',
      accountNumber: 'FR76 5555 4444 3333 2222 1111 000',
      owner: 'Pierre Bernard',
      type: 'Compte Pro',
      balance: 25780.00,
      status: 'Gelé',
      createdAt: new Date('2023-12-10'),
      lastTransaction: new Date('2024-11-15')
    },
    {
      id: 'ACC004',
      accountNumber: 'FR76 1111 2222 3333 4444 5555 666',
      owner: 'Sophie Lefebvre',
      type: 'Compte Courant',
      balance: 3200.75,
      status: 'Actif',
      createdAt: new Date('2024-03-05'),
      lastTransaction: new Date('2024-11-29')
    }
  ];

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource(this.accounts);
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  viewAccount(account: BankAccount): void {
    console.log('View account:', account);
  }

  editAccount(account: BankAccount): void {
    console.log('Edit account:', account);
  }

  freezeAccount(account: BankAccount): void {
    console.log('Freeze account:', account);
  }

  closeAccount(account: BankAccount): void {
    console.log('Close account:', account);
  }

  addAccount(): void {
    console.log('Add new account');
  }
}
