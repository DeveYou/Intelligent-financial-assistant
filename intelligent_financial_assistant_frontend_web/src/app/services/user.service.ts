import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly API_URL = 'http://localhost:8080/auth-service/admin/users/';
  private readonly API_URL_files = 'http://localhost:8080/auth-service/files';

  constructor(private http: HttpClient) { }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.API_URL}`);
  }

  createUser(user: any): Observable<User> {
    return this.http.post<User>(`${this.API_URL}`, user);
  }

  updateUser(userId: number, user: any): Observable<User> {
    return this.http.patch<User>(`${this.API_URL}${userId}`, user);
  }

  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}${userId}`);
  }

  getUserById(userId: number): Observable<User> {
    return this.http.get<User>(`${this.API_URL}${userId}`);
  }

  uploadProfileImage(userId: number, file: File): Observable<User> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<User>(`${this.API_URL}${userId}/profile-image`, formData);
  }

  uploadFile(file: File): Observable<{ fileName: string; fileUrl: string; fileType: string; size: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ fileName: string; fileUrl: string; fileType: string; size: string }>(
      `${this.API_URL_files}/upload`,
      formData
    );
  }
}

