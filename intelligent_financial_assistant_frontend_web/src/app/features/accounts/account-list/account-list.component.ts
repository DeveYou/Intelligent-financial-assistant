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
import { BankAccount } from '../../../models/bank-account.model';
import { AccountService } from '../../../services/account.service';


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
  accounts: BankAccount[] = [];
  dataSource = new MatTableDataSource<BankAccount>();
  displayedColumns: string[] = ['iban', 'type', 'balance', 'createdAt', 'isActive', 'actions'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private accountService: AccountService
  ) { }
  

  ngOnInit(): void {
    this.loadAccounts();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  loadAccounts(): void {
        this.accountService.getAccounts().subscribe({
            next: (accounts) => {
                this.accounts = accounts;
                this.dataSource.data = accounts;
            },
            error: (err) => {
                console.error('Error loading accounts', err);
            }
        });
    }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  addAccount() {
    // TODO: Implement add account logic or navigation
    console.log('Add account clicked');
  }

  viewAccount(account: BankAccount) {
    console.log('View account', account);
  }

  editAccount(account: BankAccount) {
    console.log('Edit account', account);
  }

  freezeAccount(account: BankAccount) {
    console.log('Freeze account', account);
  }

  closeAccount(account: BankAccount) {
    console.log('Close account', account);
  }
}
