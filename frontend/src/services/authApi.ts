import { useMutation, useQuery } from '@tanstack/react-query';
import { AuthResponse, LoginRequest, RegisterRequest, UserInfo } from '@/types/auth';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

// ── Raw API functions ────────────────────────────────────────────────

export async function loginUser(credentials: LoginRequest): Promise<AuthResponse> {
  const response = await fetch(`${API_BASE_URL}/api/v1/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(credentials),
  });
  if (!response.ok) {
    const error = await response.json().catch(() => ({}));
    throw new Error(error?.message || 'Invalid email or password');
  }
  return response.json();
}

export async function registerUser(data: RegisterRequest): Promise<UserInfo> {
  const response = await fetch(`${API_BASE_URL}/api/v1/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    const error = await response.json().catch(() => ({}));
    throw new Error(error?.message || 'Registration failed. Email may already be in use.');
  }
  return response.json();
}

export async function getCurrentUser(token: string): Promise<UserInfo> {
  const response = await fetch(`${API_BASE_URL}/api/v1/auth/me`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!response.ok) throw new Error('Failed to fetch current user');
  return response.json();
}

// ── TanStack Query Hooks ─────────────────────────────────────────────

export function useLogin() {
  return useMutation({
    mutationFn: loginUser,
  });
}

export function useRegister() {
  return useMutation({
    mutationFn: registerUser,
  });
}

export function useCurrentUser(token: string | null) {
  return useQuery({
    queryKey: ['currentUser', token],
    queryFn: () => getCurrentUser(token!),
    enabled: !!token,
    staleTime: 1000 * 60 * 15, // 15 minutes — matches access token TTL
    retry: false,
  });
}
