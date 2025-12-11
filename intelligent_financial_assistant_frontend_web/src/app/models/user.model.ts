// Interfaces pour les utilisateurs
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
  role?: string;
}
