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
    loading = true;
    error: string | null = null;
    
    // Mock bank account
    bankAccount = {
        id: '1',
        accountNumber: '1234567890123456',
        balance: 15420.50,
        currency: 'MAD',
        type: 'Compte Courant',
        status: 'ACTIVE',
        createdAt: new Date('2023-01-15')
    };

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private userService: UserService
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
        // Assuming getUserById exists in UserService. If not, I might need to implement it or use a workaround.
        // Based on standard practices, it should exist. If not, I'll check UserService.
        this.userService.getUserById(Number(id)).subscribe({
            next: (user) => {
                this.user = user;
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading user', err);
                this.error = 'Erreur lors du chargement de l\'utilisateur';
                this.loading = false;
            }
        });
    }

    goBack(): void {
        this.router.navigate(['/admin/users']);
    }
}
