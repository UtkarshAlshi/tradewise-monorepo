"use client";

import Link from "next/link";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useAuthActions } from "@/hooks/use-auth";
import { toErrorMessage } from "@/lib/utils/errors";

const schema = z.object({
  email: z.string().email(),
  password: z.string().min(8),
});

type FormValues = z.infer<typeof schema>;

export function LoginForm() {
  const { loginMutation } = useAuthActions();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({ resolver: zodResolver(schema) });

  return (
    <form className="panel w-full max-w-md p-8" onSubmit={handleSubmit((values) => loginMutation.mutate(values))}>
      <div className="mb-6">
        <p className="text-2xl font-semibold text-slate-100">Login to TradeWise</p>
        <p className="mt-2 text-sm text-muted">Run strategy backtests and manage your portfolios.</p>
      </div>

      <div className="mb-4">
        <label className="field-label">Email</label>
        <input className="field-input" type="email" {...register("email")} />
        {errors.email ? <p className="mt-2 text-xs text-red-300">{errors.email.message}</p> : null}
      </div>

      <div className="mb-6">
        <label className="field-label">Password</label>
        <input className="field-input" type="password" {...register("password")} />
        {errors.password ? <p className="mt-2 text-xs text-red-300">{errors.password.message}</p> : null}
      </div>

      {loginMutation.isError ? <p className="mb-4 text-sm text-red-300">{toErrorMessage(loginMutation.error)}</p> : null}

      <button className="primary-btn w-full" type="submit" disabled={loginMutation.isPending}>
        {loginMutation.isPending ? "Logging in..." : "Login"}
      </button>

      <p className="mt-4 text-center text-sm text-muted">
        New here? <Link className="text-brand" href="/register">Create an account</Link>
      </p>
    </form>
  );
}
