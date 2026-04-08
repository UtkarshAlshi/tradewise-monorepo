"use client";

import { useQuery } from "@tanstack/react-query";
import { getCurrentUser } from "@/lib/api/auth";
import { useAuthStore } from "@/store/auth-store";

export function useCurrentUser() {
  const token = useAuthStore((state) => state.token);

  return useQuery({
    queryKey: ["current-user"],
    queryFn: () => getCurrentUser(token!),
    enabled: Boolean(token),
  });
}
