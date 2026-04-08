import { apiFetch } from "@/lib/api/client";
import type { BacktestReportResponse, BacktestRequest } from "@/types/backtest";

export function runBacktest(payload: BacktestRequest, token: string) {
  return apiFetch<BacktestReportResponse>("/api/backtest", {
    method: "POST",
    body: JSON.stringify(payload),
  }, token);
}
