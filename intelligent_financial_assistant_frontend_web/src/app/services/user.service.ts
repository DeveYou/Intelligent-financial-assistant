import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly API_URL = 'http://localhost:8080/auth-service/admin/users';

  constructor(private http: HttpClient) { }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.API_URL}`);
  }

  getUserCount(): Observable<number> {
    return this.http.get<number>(`${this.API_URL}/count`);
  }

  createUser(user: any): Observable<User> {
    return this.http.post<User>(`${this.API_URL}`, user);
  }

  updateUser(userId: number, user: any): Observable<User> {
    return this.http.patch<User>(`${this.API_URL}/${userId}`, user);
  }

  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${userId}`);
  }

  getUserById(userId: number): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/${userId}`);
  }
}

