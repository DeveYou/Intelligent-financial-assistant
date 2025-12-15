import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../core/auth/auth.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  currentUser: Partial<User> | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    const loginUser = this.authService.getCurrentUser();
    if (loginUser) {
      const roles = this.authService.getUserRoles();
      this.currentUser = {
        firstName: loginUser.firstName,
        lastName: loginUser.lastName,
        email: loginUser.email,
        role: roles.length > 0 ? roles[0] : undefined,
        address: loginUser.address,
        phoneNumber: loginUser.phoneNumber,
        cin: loginUser.cin
      };
    }
  }
}
