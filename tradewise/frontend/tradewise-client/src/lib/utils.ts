import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export const API_BASE_URL = "http://localhost:8000";
// Force WS to Gateway (8000)
export const WS_BASE_URL = "http://localhost:8000";
