"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { createStrategy, getStrategies } from "@/lib/api/strategies";
import { useAuthStore } from "@/store/auth-store";
import type { CreateStrategyRequest } from "@/types/strategy";

export function useStrategies() {
  const token = useAuthStore((state) => state.token);
  return useQuery({
    queryKey: ["strategies"],
    queryFn: () => getStrategies(token!),
    enabled: Boolean(token),
  });
}

export function useCreateStrategy() {
  const token = useAuthStore((state) => state.token);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: CreateStrategyRequest) => createStrategy(payload, token!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["strategies"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    },
  });
}
