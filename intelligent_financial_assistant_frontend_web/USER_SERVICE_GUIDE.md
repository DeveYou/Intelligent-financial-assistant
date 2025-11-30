# 📘 Guide d'utilisation - UserService

## 🎯 Vue d'ensemble

Le `UserService` permet d'interagir avec les endpoints utilisateurs du backend, notamment pour :
- Récupérer le profil d'un utilisateur
- Mettre à jour le profil d'un utilisateur
- Récupérer tous les utilisateurs (admin)

---

## 📁 Fichiers créés/modifiés

### 1. `user.model.ts` ✅
Interfaces TypeScript pour les utilisateurs :

```typescript
export interface UpdateProfileRequest {
  cin?: string;
  address?: string;
  phoneNumber?: string;
}

export interface UserProfile {
  firstName: string;
  lastName: string;
  cin?: string;
  email: string;
  phoneNumber?: string;
  address?: string;
  createdAt: string;
  enabled: boolean;
}

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  cin?: string;
  phoneNumber?: string;
  address?: string;
  enabled: boolean;
  createdAt: string;
}
```

### 2. `user.service.ts` ✅
Service Angular avec les méthodes :

```typescript
@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly API_URL = 'http://localhost:8080/AUTH-SERVICE';

  getUserProfile(userId: number): Observable<UserProfile>
  updateUserProfile(userId: number, updateRequest: UpdateProfileRequest): Observable<UserProfile>
  getAllUsers(): Observable<User[]>
}
```

---

## 🚀 Utilisation dans un composant

### Exemple 1 : Récupérer le profil d'un utilisateur

```typescript
import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { UserProfile } from '../../models/user.model';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit {
  userProfile: UserProfile | null = null;
  loading = false;
  error: string | null = null;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    const userId = 1; // Récupérer depuis le token ou localStorage

    this.loading = true;
    this.userService.getUserProfile(userId).subscribe({
      next: (profile) => {
        this.userProfile = profile;
        this.loading = false;
        console.log('Profil chargé:', profile);
      },
      error: (error) => {
        this.error = error?.error?.message || 'Erreur lors du chargement du profil';
        this.loading = false;
        console.error('Erreur:', error);
      }
    });
  }
}
```

### Exemple 2 : Mettre à jour le profil

```typescript
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { UpdateProfileRequest } from '../../models/user.model';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html'
})
export class EditProfileComponent {
  profileForm: FormGroup;
  loading = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private userService: UserService
  ) {
    this.profileForm = this.fb.group({
      cin: ['', [Validators.pattern(/^[A-Z]{1,2}\d{5,7}$/)]],
      address: [''],
      phoneNumber: ['', [Validators.pattern(/^(\+212|0)[5-7]\d{8}$/)]]
    });
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      return;
    }

    const userId = 1; // Récupérer depuis le token ou localStorage
    const updateRequest: UpdateProfileRequest = this.profileForm.value;

    this.loading = true;
    this.successMessage = null;
    this.errorMessage = null;

    this.userService.updateUserProfile(userId, updateRequest).subscribe({
      next: (updatedProfile) => {
        this.loading = false;
        this.successMessage = 'Profil mis à jour avec succès !';
        console.log('Profil mis à jour:', updatedProfile);
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error?.error?.message || 'Erreur lors de la mise à jour';
        console.error('Erreur:', error);
      }
    });
  }
}
```

### Exemple 3 : Récupérer tous les utilisateurs (Admin)

```typescript
import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html'
})
export class UserListComponent implements OnInit {
  users: User[] = [];
  loading = false;
  error: string | null = null;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.loading = false;
        console.log(`${users.length} utilisateurs chargés`);
      },
      error: (error) => {
        this.error = error?.error?.message || 'Erreur lors du chargement des utilisateurs';
        this.loading = false;
        console.error('Erreur:', error);
      }
    });
  }
}
```

---

## 🔐 Récupérer l'ID de l'utilisateur connecté

Pour récupérer l'ID de l'utilisateur connecté, vous devez le stocker lors du login. Voici comment :

### Option 1 : Ajouter l'ID dans LoginResponse

Modifier l'interface `LoginResponse` dans `core/auth/user.model.ts` :

```typescript
export interface LoginResponse {
  message: string;
  token: string;
  type: string;
  email: string;
  firstName: string;
  lastName: string;
  userId: number;  // ✅ Ajouter l'ID
}
```

Puis dans votre composant :

```typescript
const currentUser = this.authService.getCurrentUser();
if (currentUser) {
  const userId = currentUser.userId;
  this.userService.getUserProfile(userId).subscribe(...);
}
```

### Option 2 : Décoder le token JWT

```typescript
import { jwtDecode } from 'jwt-decode';

interface JwtPayload {
  sub: string;  // email
  userId: number;
  exp: number;
  iat: number;
}

getUserIdFromToken(): number | null {
  const token = localStorage.getItem('token');
  if (!token) return null;

  try {
    const decoded = jwtDecode<JwtPayload>(token);
    return decoded.userId;
  } catch (error) {
    console.error('Erreur de décodage du token:', error);
    return null;
  }
}
```

---

## 📋 Endpoints utilisés

| Méthode | Endpoint | Description | Authentification |
|---------|----------|-------------|------------------|
| GET | `/users/{userId}/profile` | Récupérer le profil | ✅ Requise |
| PATCH | `/users/{userId}/profile` | Mettre à jour le profil | ✅ Requise |
| GET | `/admin/users` | Liste tous les utilisateurs | ✅ Admin requis |

---

## 🧪 Test avec Angular

### 1. Importer le service

```typescript
import { UserService } from './services/user.service';
```

### 2. L'injecter dans votre composant

```typescript
constructor(private userService: UserService) {}
```

### 3. Utiliser les méthodes

```typescript
// Récupérer le profil
this.userService.getUserProfile(1).subscribe(profile => {
  console.log(profile);
});

// Mettre à jour le profil
const updateData = { cin: 'AB123456', phoneNumber: '0612345678' };
this.userService.updateUserProfile(1, updateData).subscribe(updated => {
  console.log('Mis à jour:', updated);
});

// Liste des utilisateurs (admin)
this.userService.getAllUsers().subscribe(users => {
  console.log('Utilisateurs:', users);
});
```

---

## ✅ Validation des données

### Patterns de validation recommandés

```typescript
// CIN marocain : 1-2 lettres + 5-7 chiffres
cin: ['', [Validators.pattern(/^[A-Z]{1,2}\d{5,7}$/)]]

// Téléphone marocain : +212 ou 0, puis 5/6/7 suivi de 8 chiffres
phoneNumber: ['', [Validators.pattern(/^(\+212|0)[5-7]\d{8}$/)]]

// Adresse : minimum 10 caractères
address: ['', [Validators.minLength(10), Validators.maxLength(200)]]
```

---

## 🎨 Exemple de template HTML

```html
<mat-card>
  <mat-card-header>
    <mat-card-title>Mon Profil</mat-card-title>
  </mat-card-header>

  <mat-card-content>
    <form [formGroup]="profileForm" (ngSubmit)="onSubmit()">
      
      <mat-form-field appearance="outline">
        <mat-label>CIN</mat-label>
        <input matInput formControlName="cin" placeholder="AB123456">
        <mat-error *ngIf="profileForm.get('cin')?.hasError('pattern')">
          Format invalide (ex: AB123456)
        </mat-error>
      </mat-form-field>

      <mat-form-field appearance="outline">
        <mat-label>Adresse</mat-label>
        <textarea matInput formControlName="address" rows="3"></textarea>
      </mat-form-field>

      <mat-form-field appearance="outline">
        <mat-label>Téléphone</mat-label>
        <input matInput formControlName="phoneNumber" placeholder="0612345678">
        <mat-error *ngIf="profileForm.get('phoneNumber')?.hasError('pattern')">
          Format invalide (ex: 0612345678)
        </mat-error>
      </mat-form-field>

      <button mat-raised-button color="primary" 
              type="submit" 
              [disabled]="profileForm.invalid || loading">
        <mat-spinner *ngIf="loading" diameter="20"></mat-spinner>
        {{ loading ? 'Mise à jour...' : 'Enregistrer' }}
      </button>

    </form>
  </mat-card-content>
</mat-card>
```

---

## 🔧 Gestion des erreurs

```typescript
this.userService.getUserProfile(userId).subscribe({
  next: (profile) => {
    // Succès
    console.log('Profil:', profile);
  },
  error: (error) => {
    // Gérer les différents codes d'erreur
    if (error.status === 404) {
      console.error('Utilisateur non trouvé');
    } else if (error.status === 403) {
      console.error('Accès refusé');
    } else if (error.status === 401) {
      console.error('Non authentifié');
      // Rediriger vers login
    } else {
      console.error('Erreur serveur:', error.message);
    }
  }
});
```

---

## ✅ Résumé

Le `UserService` est maintenant prêt à être utilisé ! 

**Fonctionnalités disponibles** :
- ✅ Récupération du profil utilisateur
- ✅ Mise à jour du profil (CIN, adresse, téléphone)
- ✅ Liste de tous les utilisateurs (admin)
- ✅ Interfaces TypeScript typées
- ✅ Gestion automatique du token (via intercepteur)

**Pour utiliser** :
1. Importer `UserService` dans votre composant
2. L'injecter dans le constructor
3. Appeler les méthodes avec l'ID utilisateur

**Documentation backend** : Consultez le Swagger à `http://localhost:8080/swagger-ui.html`

