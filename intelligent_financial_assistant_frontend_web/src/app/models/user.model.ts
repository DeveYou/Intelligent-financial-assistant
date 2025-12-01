// Interfaces pour les utilisateurs

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
  profileImage?: string;
}
