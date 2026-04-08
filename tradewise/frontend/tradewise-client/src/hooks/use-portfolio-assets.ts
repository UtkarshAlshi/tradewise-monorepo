"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { addAsset, deleteAsset, getPortfolioAssets } from "@/lib/api/portfolios";
import { useAuthStore } from "@/store/auth-store";

export function usePortfolioAssets(portfolioId: string) {
  const token = useAuthStore((state) => state.token);
  return useQuery({
    queryKey: ["portfolio-assets", portfolioId],
    queryFn: () => getPortfolioAssets(portfolioId, token!),
    enabled: Boolean(token && portfolioId),
  });
}

export function useAddAsset(portfolioId: string) {
  const token = useAuthStore((state) => state.token);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: { symbol: string; quantity: number; purchasePrice: number; purchaseDate: string }) =>
      addAsset(portfolioId, payload, token!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["portfolio-assets", portfolioId] });
    },
  });
}

export function useDeleteAsset(portfolioId: string) {
  const token = useAuthStore((state) => state.token);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (assetId: string) => deleteAsset(portfolioId, assetId, token!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["portfolio-assets", portfolioId] });
    },
  });
}
