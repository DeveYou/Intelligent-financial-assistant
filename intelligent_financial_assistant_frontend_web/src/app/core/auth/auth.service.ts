import {Inject, Injectable, PLATFORM_ID} from '@angular/core';
import {BehaviorSubject, catchError, map, Observable, of, tap} from "rxjs";
import {LoginRequest, LoginResponse} from "./user.model";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {isPlatformBrowser} from "@angular/common";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly AUTH_API_URL = 'http://localhost:8080/AUTH-SERVICE/auth';

  private currentUserSubject = new BehaviorSubject<LoginResponse | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();
  private isBrowser = false;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
    if (this.isBrowser) {
      const storedUser = localStorage.getItem('currentUser');
      if (storedUser) {
        this.currentUserSubject.next(JSON.parse(storedUser));
      }
    }
  }

  login(email: string, password: string): Observable<LoginResponse> {
    const body: LoginRequest = {email, password};
    return this.http.post<LoginResponse>(`${this.AUTH_API_URL}/login`, body).pipe(
      map((res) => {
        if (res && res.token && this.isBrowser) {
          localStorage.setItem('token', res.token);
          localStorage.setItem('currentUser', JSON.stringify(res));
        }
        this.currentUserSubject.next(res);
        return res;
      })
    );
  }

  logout() : Observable<any>{
    if (!this.isBrowser) {
      return of(null);
    }

    const token = localStorage.getItem('token');
    if (!token) {
      this.clearSession();
      return of(null);
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    return this.http.post(`${this.AUTH_API_URL}/logout`, {}, {
      headers,
      responseType: 'text'  // ✅ Accepter une réponse en texte brut
    }).pipe(
      tap(() => {
        this.clearSession();
      }),
      catchError((error) => {
        console.error('Logout error:', error);
        this.clearSession();
        return of(null);
      })
    );
  }

  private clearSession() {
    if (this.isBrowser) {
      localStorage.removeItem('token');
      localStorage.removeItem('currentUser');
    }
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    if (!this.isBrowser) {
      return false;
    }
    return !!localStorage.getItem('token');
  }

  getToken(): string | null {
    if (!this.isBrowser) {
      return null;
    }
    return localStorage.getItem('token');
  }

  getCurrentUser(): LoginResponse | null {
    return this.currentUserSubject.value;
  }

  validateToken(): Observable<boolean> {
    if (!this.isBrowser) {
      return of(false);
    }

    const token = localStorage.getItem('token');
    if (!token) {
      return of(false);
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    return this.http.get<boolean>(`${this.AUTH_API_URL}/validate-token`, {headers}).pipe(
      map((isValid) => {
        if (!isValid) {
          this.clearSession();
        }
        return isValid;
      }),
      catchError((error) => {
        console.error('Token validation error:', error);
        this.clearSession();
        return of(false);
      })
    );
  }
}
