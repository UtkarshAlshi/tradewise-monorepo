"use client";

import { useMutation } from "@tanstack/react-query";
import { runBacktest } from "@/lib/api/backtest";
import { useAuthStore } from "@/store/auth-store";
import type { BacktestRequest } from "@/types/backtest";

export function useRunBacktest() {
  const token = useAuthStore((state) => state.token);

  return useMutation({
    mutationFn: (payload: BacktestRequest) => runBacktest(payload, token!),
  });
}
