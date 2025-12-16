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
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDivider } from "@angular/material/divider";
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
    MatDivider,
    MatSnackBarModule
  ],
  templateUrl: './account-list.component.html',
  styleUrls: ['./account-list.component.css']
})
export class AccountListComponent implements OnInit {
  accounts: BankAccount[] = [];
  dataSource = new MatTableDataSource<BankAccount>();
  displayedColumns: string[] = ['rib', 'type', 'balance', 'createdAt', 'isActive', 'actions'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private accountService: AccountService,
    private snackBar: MatSnackBar
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
                this.snackBar.open('Erreur lors du chargement des comptes', 'Fermer', { duration: 3000 });
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
    // TODO: Implement edit dialog
    console.log('Edit account', account);
    this.snackBar.open('Fonctionnalité de modification à venir', 'Fermer', { duration: 2000 });
  }

  freezeAccount(account: BankAccount) {
    const updatedStatus = !account.isActive;
    this.accountService.updateAccount(account.id, { isActive: updatedStatus }).subscribe({
      next: (updatedAccount) => {
        const index = this.accounts.findIndex(a => a.id === updatedAccount.id);
        if (index !== -1) {
          this.accounts[index] = updatedAccount;
          this.dataSource.data = [...this.accounts]; // Refresh table
        }
        const message = updatedStatus ? 'Compte activé' : 'Compte gelé';
        this.snackBar.open(message, 'Fermer', { duration: 3000 });
      },
      error: (err) => {
        console.error('Error updating account status', err);
        this.snackBar.open('Erreur lors de la mise à jour du statut', 'Fermer', { duration: 3000 });
      }
    });
  }

  closeAccount(account: BankAccount) {
    if (confirm(`Êtes-vous sûr de vouloir clôturer le compte ${account.iban} ?`)) {
      this.accountService.deleteAccount(account.id).subscribe({
        next: () => {
          this.accounts = this.accounts.filter(a => a.id !== account.id);
          this.dataSource.data = this.accounts;
          this.snackBar.open('Compte clôturé avec succès', 'Fermer', { duration: 3000 });
        },
        error: (err) => {
          console.error('Error deleting account', err);
          this.snackBar.open('Erreur lors de la clôture du compte', 'Fermer', { duration: 3000 });
        }
      });
    }
  }
}
