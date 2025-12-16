import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();

  // Ne pas ajouter le token pour les endpoints publics (login, register)
  if (req.url.includes('/login') || req.url.includes('/register')) {
    return next(req);
  }

  // Ajouter le token dans le header Authorization si disponible
  if (token) {
    const clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    return next(clonedReq).pipe(
      catchError((error: HttpErrorResponse) => {
        // Si erreur 401 (Unauthorized), nettoyer la session et rediriger
        if (error.status === 401) {
          authService.logout().subscribe({
            complete: () => {
              router.navigate(['/auth/login']);
            }
          });
        } else if (error.status === 403) {
          router.navigate(['/access-denied']);
        }
        return throwError(() => error);
      })
    );
  }

  return next(req);
};

