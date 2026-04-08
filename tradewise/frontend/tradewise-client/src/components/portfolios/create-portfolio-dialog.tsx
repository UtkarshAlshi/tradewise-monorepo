"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useCreatePortfolio } from "@/hooks/use-portfolios";
import { toErrorMessage } from "@/lib/utils/errors";

const schema = z.object({
  name: z.string().min(2).max(100),
  description: z.string().max(500).optional(),
});

type FormValues = z.infer<typeof schema>;

export function CreatePortfolioDialog() {
  const [open, setOpen] = useState(false);
  const mutation = useCreatePortfolio();
  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { name: "", description: "" },
  });

  if (!open) {
    return <button className="primary-btn" type="button" onClick={() => setOpen(true)}>Create portfolio</button>;
  }

  return (
    <div className="panel p-5">
      <div className="mb-4 flex items-center justify-between">
        <div>
          <p className="text-lg font-semibold text-slate-100">Create portfolio</p>
          <p className="text-sm text-muted">Start tracking a basket of assets.</p>
        </div>
        <button className="secondary-btn" type="button" onClick={() => setOpen(false)}>Close</button>
      </div>
      <form onSubmit={handleSubmit((values) => mutation.mutate(values, {
        onSuccess: () => {
          reset();
          setOpen(false);
        },
      }))} className="grid gap-4 md:grid-cols-2">
        <div>
          <label className="field-label">Name</label>
          <input className="field-input" {...register("name")} />
          {errors.name ? <p className="mt-1 text-xs text-red-300">{errors.name.message}</p> : null}
        </div>
        <div>
          <label className="field-label">Description</label>
          <input className="field-input" {...register("description")} />
          {errors.description ? <p className="mt-1 text-xs text-red-300">{errors.description.message}</p> : null}
        </div>
        <div className="md:col-span-2 flex items-center gap-3">
          <button className="primary-btn" type="submit" disabled={mutation.isPending}>{mutation.isPending ? "Creating..." : "Create portfolio"}</button>
          {mutation.isError ? <p className="text-sm text-red-300">{toErrorMessage(mutation.error)}</p> : null}
        </div>
      </form>
    </div>
  );
}
