"use client";

import { useQuery } from "@tanstack/react-query";
import { getDashboardAnalytics } from "@/lib/api/dashboard";
import { useAuthStore } from "@/store/auth-store";

export function useDashboard() {
  const token = useAuthStore((state) => state.token);

  return useQuery({
    queryKey: ["dashboard"],
    queryFn: () => getDashboardAnalytics(token!),
    enabled: Boolean(token),
  });
}
