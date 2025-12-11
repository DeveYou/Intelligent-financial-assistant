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
import { ImageUploadComponent } from '../../../shared/components/image-upload/image-upload.component';

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
        ImageUploadComponent
    ],
    templateUrl: './user-form.component.html',
    styleUrls: ['./user-form.component.css']
})
export class UserFormComponent implements OnInit {
    userForm: FormGroup;
    isEditMode: boolean = false;
    selectedImageFile: File | null = null;
    uploadingImage = false;
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
            cin: [''],
            phoneNumber: [''],
            address: [''],
            role: ['ROLE_USER']
        });
    }

    ngOnInit(): void {
        if (this.data && this.data.user) {
            this.isEditMode = true;
            // profileImage may not be declared on User type -> use a safe assertion
            const userAny = this.data.user as any;
            if (userAny && userAny.profileImage) {
                this.currentImageUrl = userAny.profileImage;
            }
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
                        // Si une image a été sélectionnée, l'uploader
                        if (this.selectedImageFile) {
                            this.uploadImage(this.data.user!.id);
                        } else {
                            this.snackBar.open('Utilisateur mis à jour avec succès', 'Fermer', {
                                duration: 3000
                            });
                            this.dialogRef.close(user);
                        }
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
                        // Si une image a été sélectionnée, l'uploader
                        if (this.selectedImageFile) {
                            this.uploadImage(user.id);
                        } else {
                            this.snackBar.open('Utilisateur créé avec succès', 'Fermer', {
                                duration: 3000
                            });
                            this.dialogRef.close(user);
                        }
                    },
                    error: (err) => {
                        console.error('Error creating user', err);
                        const errorMessage = err?.error?.message || 'Erreur lors de la création de l\'utilisateur';
                        this.snackBar.open(errorMessage, 'Fermer', {
                            duration: 5000,
                            panelClass: ['error-snackbar']
                        });
                    }
                });
            }
        }
    }

    uploadImage(userId: number): void {
        if (!this.selectedImageFile) return;

        this.uploadingImage = true;
        this.userService.uploadProfileImage(userId, this.selectedImageFile).subscribe({
            next: (user) => {
                this.uploadingImage = false;
                this.snackBar.open(
                    this.isEditMode ? 'Utilisateur et image mis à jour avec succès' : 'Utilisateur créé avec succès',
                    'Fermer',
                    { duration: 3000 }
                );
                this.dialogRef.close(user);
            },
            error: (err) => {
                this.uploadingImage = false;
                console.error('Error uploading image', err);
                this.snackBar.open('Erreur lors de l\'upload de l\'image', 'Fermer', {
                    duration: 3000,
                    panelClass: ['error-snackbar']
                });
            }
        });
    }

    onImageSelected(file: File): void {
        this.selectedImageFile = file;
    }

    onImageRemoved(): void {
        this.selectedImageFile = null;
    }

    onCancel(): void {
        this.dialogRef.close();
    }
}
