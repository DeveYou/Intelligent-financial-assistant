import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UpdateProfileRequest, UserProfile, User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly API_URL = 'http://localhost:8080/AUTH-SERVICE';

  constructor(private http: HttpClient) {}

  getUserProfile(userId: number): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.API_URL}/users/${userId}/profile`);
  }

  updateUserProfile(userId: number, updateRequest: UpdateProfileRequest): Observable<UserProfile> {
    return this.http.patch<UserProfile>(`${this.API_URL}/users/${userId}/profile`, updateRequest);
  }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.API_URL}/admin/users`);
  }
}


