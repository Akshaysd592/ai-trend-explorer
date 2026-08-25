// Auth domain types — mirrors auth-service OpenAPI DTOs

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface UserInfo {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  enabled: boolean;
  roles: string[];
  createdAt: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresInSeconds: number;
  user: UserInfo;
}
