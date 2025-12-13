import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { User } from '../../../models/user.model';
import { UserService } from '../../../services/user.service';

@Component({
    selector: 'app-user-form',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatSelectModule,
        MatSnackBarModule,
        MatTooltipModule,
        MatDividerModule,
    ],
    templateUrl: './user-form.component.html',
    styleUrls: ['./user-form.component.css']
})
export class UserFormComponent implements OnInit {
    userForm: FormGroup;
    isEditMode: boolean = false;
    currentImageUrl?: string;

    constructor(
        private fb: FormBuilder,
        private userService: UserService,
        private snackBar: MatSnackBar,
        public dialogRef: MatDialogRef<UserFormComponent>,
        @Inject(MAT_DIALOG_DATA) public data: { user?: User }
    ) {
        this.userForm = this.fb.group({
            firstName: ['', Validators.required],
            lastName: ['', Validators.required],
            email: ['', [Validators.required, Validators.email]],
            password: [''],
            cin: ['', Validators.required],
            phoneNumber: [''],
            address: [''],
            type: ['CURRENT_ACCOUNT', Validators.required],
            role: ['ROLE_USER']
        });
    }

    ngOnInit(): void {
        if (this.data && this.data.user) {
            this.isEditMode = true;
            this.userForm.patchValue(this.data.user);
            // Password is not editable directly here usually, or optional
            this.userForm.get('password')?.clearValidators();
            this.userForm.get('password')?.updateValueAndValidity();
        } else {
            this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
            this.userForm.get('password')?.updateValueAndValidity();
        }
    }

    onSubmit(): void {
        if (this.userForm.valid) {
            if (this.isEditMode && this.data.user?.id) {
                this.userService.updateUser(this.data.user.id, this.userForm.value).subscribe({
                    next: (user) => {
                            this.snackBar.open('Utilisateur mis à jour avec succès', 'Fermer', {
                                duration: 3000
                            });
                            this.dialogRef.close(user);
                    },
                    error: (err) => {
                        console.error('Error updating user', err);
                        this.snackBar.open('Erreur lors de la mise à jour de l\'utilisateur', 'Fermer', {
                            duration: 3000,
                            panelClass: ['error-snackbar']
                        });
                    }
                });
            } else {
                this.userService.createUser(this.userForm.value).subscribe({
                    next: (user) => {
                            this.snackBar.open('Utilisateur créé avec succès', 'Fermer', {
                                duration: 3000
                            });
                            this.dialogRef.close(user);
                    },
                    error: (err) => {
                        console.error('Error creating user', err);
                        if (err.status === 409) {
                            const message = err.error?.message || '';
                            
                            if (message.toLowerCase().includes('cin')) {
                                const cinControl = this.userForm.get('cin');
                                cinControl?.setErrors({ cinTaken: true });
                                cinControl?.markAsTouched();
                                this.snackBar.open('Ce CIN est déjà utilisé', 'Fermer', {
                                    duration: 5000,
                                    panelClass: ['error-snackbar']
                                });
                            } else {
                                const emailControl = this.userForm.get('email');
                                emailControl?.setErrors({ emailTaken: true });
                                emailControl?.markAsTouched();
                                this.snackBar.open('Cet email est déjà utilisé', 'Fermer', {
                                    duration: 5000,
                                    panelClass: ['error-snackbar']
                                });
                            }
                        } else {
                            const errorMessage = err?.error?.message || 'Erreur lors de la création de l\'utilisateur';
                            this.snackBar.open(errorMessage, 'Fermer', {
                                duration: 5000,
                                panelClass: ['error-snackbar']
                            });
                        }
                    }
                });
            }
        }
    }

    onCancel(): void {
        this.dialogRef.close();
    }
}
