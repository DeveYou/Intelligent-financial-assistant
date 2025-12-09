import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from './auth.service';
import { map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    router.navigate(['/auth/login'], {
      queryParams: { returnUrl: state.url }
    });
    return false;
  }

  return authService.validateToken().pipe(
    map((isValid) => {
      if (isValid) {
        const requiredRoles = route.data['roles'] as Array<string>;
        if (requiredRoles && requiredRoles.length > 0) {
          const userRoles = authService.getUserRoles();
          const hasRole = requiredRoles.some(role => userRoles.includes(role));
          if (!hasRole) {
            router.navigate(['/access-denied']);
            return false;
          }
        }
        return true;
      }

      router.navigate(['/auth/login'], {
        queryParams: { returnUrl: state.url }
      });
      return false;
    }),
    catchError(() => {
      router.navigate(['/auth/login'], {
        queryParams: { returnUrl: state.url }
      });
      return of(false);
    })
  );
};

