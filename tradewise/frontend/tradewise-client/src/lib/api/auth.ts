import { apiFetch } from "@/lib/api/client";
import type { LoginResponse, UserResponse } from "@/types/auth";

export type RegisterRequest = {
  email: string;
  password: string;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export function registerUser(payload: RegisterRequest) {
  return apiFetch<UserResponse>("/api/auth/register", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export function loginUser(payload: LoginRequest) {
  return apiFetch<LoginResponse>("/api/auth/login", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export function getCurrentUser(token: string) {
  return apiFetch<UserResponse>("/api/users/me", {}, token);
}
