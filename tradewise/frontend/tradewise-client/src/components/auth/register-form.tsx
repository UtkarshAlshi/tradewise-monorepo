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

export function RegisterForm() {
  const { registerMutation } = useAuthActions();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormValues>({ resolver: zodResolver(schema) });

  return (
    <form
      className="panel w-full max-w-md p-8"
      onSubmit={handleSubmit((values) => {
        registerMutation.mutate(values, {
          onSuccess: () => reset(),
        });
      })}
    >
      <div className="mb-6">
        <p className="text-2xl font-semibold text-slate-100">Create your account</p>
        <p className="mt-2 text-sm text-muted">Start building strategies and testing ideas.</p>
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

      {registerMutation.isError ? <p className="mb-4 text-sm text-red-300">{toErrorMessage(registerMutation.error)}</p> : null}
      {registerMutation.isSuccess ? <p className="mb-4 text-sm text-green-300">Account created. You can log in now.</p> : null}

      <button className="primary-btn w-full" type="submit" disabled={registerMutation.isPending}>
        {registerMutation.isPending ? "Creating account..." : "Create account"}
      </button>

      <p className="mt-4 text-center text-sm text-muted">
        Already have an account? <Link className="text-brand" href="/login">Login</Link>
      </p>
    </form>
  );
}
