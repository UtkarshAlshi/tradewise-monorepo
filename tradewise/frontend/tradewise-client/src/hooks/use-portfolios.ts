"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { createPortfolio, getPortfolios } from "@/lib/api/portfolios";
import { useAuthStore } from "@/store/auth-store";

export function usePortfolios() {
  const token = useAuthStore((state) => state.token);
  return useQuery({
    queryKey: ["portfolios"],
    queryFn: () => getPortfolios(token!),
    enabled: Boolean(token),
  });
}

export function useCreatePortfolio() {
  const token = useAuthStore((state) => state.token);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: { name: string; description?: string }) => createPortfolio(payload, token!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["portfolios"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    },
  });
}
