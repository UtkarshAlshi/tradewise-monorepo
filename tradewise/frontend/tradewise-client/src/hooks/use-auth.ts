"use client";

import { useRouter } from "next/navigation";
import { useMutation } from "@tanstack/react-query";
import { loginUser, registerUser } from "@/lib/api/auth";
import { useAuthStore } from "@/store/auth-store";

export function useAuthActions() {
  const router = useRouter();
  const setSession = useAuthStore((state) => state.setSession);

  const loginMutation = useMutation({
    mutationFn: loginUser,
    onSuccess: (data) => {
      setSession(data.token, data.user);
      router.replace("/dashboard");
    },
  });

  const registerMutation = useMutation({
    mutationFn: registerUser,
  });

  return { loginMutation, registerMutation };
}
