import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { User } from '../../../models/user.model';
import { UserService } from '../../../services/user.service';
import { AccountService } from '../../../services/account.service';
import { BankAccount } from '../../../models/bank-account.model';

@Component({
    selector: 'app-user-details',
    standalone: true,
    imports: [
        CommonModule, 
        MatButtonModule, 
        MatIconModule, 
        MatCardModule, 
        MatDividerModule,
        MatProgressSpinnerModule
    ],
    templateUrl: './user-details.component.html',
    styleUrls: ['./user-details.component.css']
})
export class UserDetailsComponent implements OnInit {
    user: User | null = null;
    accounts: BankAccount[] = [];
    loading = true;
    error: string | null = null;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private userService: UserService,
        private accountService: AccountService
    ) { }

    ngOnInit(): void {
        const userId = this.route.snapshot.paramMap.get('id');
        if (userId) {
            this.loadUser(userId);
        } else {
            this.error = 'Utilisateur non trouvé';
            this.loading = false;
        }
    }

    loadUser(id: string): void {
        this.loading = true;
        this.userService.getUserById(Number(id)).subscribe({
            next: (user) => {
                this.user = user;
                this.loadAccounts(user.id);
            },
            error: (err) => {
                console.error('Error loading user', err);
                this.error = 'Erreur lors du chargement de l\'utilisateur';
                this.loading = false;
            }
        });
    }

    loadAccounts(userId: number): void {
        this.accountService.getAccountsByUserId(userId).subscribe({
            next: (accounts) => {
                this.accounts = accounts;
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading accounts', err);
                // We don't block the UI if accounts fail to load, just show user info
                this.loading = false;
            }
        });
    }

    canCreateAccount(): boolean {
        return this.accounts.length < 2;
    }

    getMissingAccountType(): string {
        const hasCurrent = this.accounts.some(acc => acc.type === 'CURRENT_ACCOUNT');
        return hasCurrent ? 'SAVING_ACCOUNT' : 'CURRENT_ACCOUNT';
    }

    getMissingAccountLabel(): string {
        const type = this.getMissingAccountType();
        return type === 'CURRENT_ACCOUNT' ? 'Compte Courant' : 'Compte Épargne';
    }

    createMissingAccount(): void {
        if (!this.user || !this.canCreateAccount()) return;

        const type = this.getMissingAccountType();
        const newAccount = {
            userId: this.user.id,
            type: type,
            overDraft: type === 'CURRENT_ACCOUNT' ? 1000 : undefined,
            interestRate: type === 'SAVING_ACCOUNT' ? 3.5 : undefined,
            iban: this.generateIban(),
            expirationDate: new Date(new Date().setFullYear(new Date().getFullYear() + 4)).toISOString().split('T')[0]
        };

        this.loading = true;
        this.accountService.createAccount(newAccount).subscribe({
            next: (account) => {
                this.accounts.push(account);
                this.loading = false;
            },
            error: (err) => {
                console.error('Error creating account', err);
                this.error = 'Erreur lors de la création du compte';
                this.loading = false;
            }
        });
    }

    private generateIban(): string {
        let iban = '';
        for (let i = 0; i < 16; i++) {
            iban += Math.floor(Math.random() * 10);
        }
        return iban;
    }

    goBack(): void {
        this.router.navigate(['/admin/users']);
    }
}
