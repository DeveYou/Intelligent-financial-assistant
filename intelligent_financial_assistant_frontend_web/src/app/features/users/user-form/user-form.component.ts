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
        MatDividerModule
    ],
    templateUrl: './user-form.component.html',
    styleUrls: ['./user-form.component.css']
})
export class UserFormComponent implements OnInit {
    userForm: FormGroup;
    isEditMode: boolean = false;

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
            cin: [''],
            phoneNumber: [''],
            address: [''],
            roles: [['ROLE_USER']]
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
            const userObservable = this.isEditMode && this.data.user?.id
                ? this.userService.updateUser(this.data.user.id, this.userForm.value)
                : this.userService.createUser(this.userForm.value);

            userObservable.subscribe({
                next: (user) => {
                    this.closeDialog(true, `Utilisateur ${this.isEditMode ? 'mis à jour' : 'créé'} avec succès`);
                },
                error: (err) => {
                    console.error('Error saving user', err);
                    this.snackBar.open('Erreur lors de l\'enregistrement', 'Fermer', {
                        duration: 3000,
                        panelClass: ['error-snackbar']
                    });
                }
            });
        }
    }

    private closeDialog(success: boolean, message: string): void {
        this.snackBar.open(message, 'Fermer', { duration: 3000 });
        this.dialogRef.close(success);
    }

    onCancel(): void {
        this.dialogRef.close();
    }
}
