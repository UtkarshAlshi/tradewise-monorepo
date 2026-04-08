import { apiFetch } from "@/lib/api/client";
import type { CreateStrategyRequest, StrategyResponse } from "@/types/strategy";

export function getStrategies(token: string) {
  return apiFetch<StrategyResponse[]>("/api/strategies", {}, token);
}

export function createStrategy(payload: CreateStrategyRequest, token: string) {
  return apiFetch<StrategyResponse>("/api/strategies", {
    method: "POST",
    body: JSON.stringify(payload),
  }, token);
}
