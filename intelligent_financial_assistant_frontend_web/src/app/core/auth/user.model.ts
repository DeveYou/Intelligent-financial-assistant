export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  message: string;
  token: string;
  type: string;
  email: string;
  firstName: string;
  lastName: string;
  id: number;
  address?: string;
  phoneNumber?: string;
  cin?: string;
}
