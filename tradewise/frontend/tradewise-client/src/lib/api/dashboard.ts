import { apiFetch } from "@/lib/api/client";
import type { DashboardAnalyticsResponse } from "@/types/dashboard";

export function getDashboardAnalytics(token: string) {
  return apiFetch<DashboardAnalyticsResponse>("/api/dashboard/analytics", {}, token);
}
