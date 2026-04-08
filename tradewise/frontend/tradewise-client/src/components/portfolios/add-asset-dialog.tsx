"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useAddAsset } from "@/hooks/use-portfolio-assets";
import { toErrorMessage } from "@/lib/utils/errors";

const schema = z.object({
  symbol: z.string().min(1),
  quantity: z.coerce.number().positive(),
  purchasePrice: z.coerce.number().positive(),
  purchaseDate: z.string().min(1),
});

type FormValues = z.infer<typeof schema>;

export function AddAssetDialog({ portfolioId }: { portfolioId: string }) {
  const [open, setOpen] = useState(false);
  const mutation = useAddAsset(portfolioId);
  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      symbol: "IBM",
      quantity: 10,
      purchasePrice: 150,
      purchaseDate: new Date().toISOString().slice(0, 16),
    },
  });

  if (!open) {
    return <button className="primary-btn" type="button" onClick={() => setOpen(true)}>Add asset</button>;
  }

  return (
    <div className="panel p-5">
      <div className="mb-4 flex items-center justify-between">
        <div>
          <p className="text-lg font-semibold text-slate-100">Add asset</p>
          <p className="text-sm text-muted">Record a purchase in this portfolio.</p>
        </div>
        <button className="secondary-btn" type="button" onClick={() => setOpen(false)}>Close</button>
      </div>
      <form onSubmit={handleSubmit((values) => mutation.mutate(values, {
        onSuccess: () => {
          reset();
          setOpen(false);
        },
      }))} className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <div>
          <label className="field-label">Symbol</label>
          <input className="field-input" {...register("symbol")} />
          {errors.symbol ? <p className="mt-1 text-xs text-red-300">{errors.symbol.message}</p> : null}
        </div>
        <div>
          <label className="field-label">Quantity</label>
          <input className="field-input" type="number" step="any" {...register("quantity")} />
        </div>
        <div>
          <label className="field-label">Purchase price</label>
          <input className="field-input" type="number" step="any" {...register("purchasePrice")} />
        </div>
        <div>
          <label className="field-label">Purchase date</label>
          <input className="field-input" type="datetime-local" {...register("purchaseDate")} />
        </div>
        <div className="xl:col-span-4 flex items-center gap-3">
          <button className="primary-btn" type="submit" disabled={mutation.isPending}>{mutation.isPending ? "Saving..." : "Save asset"}</button>
          {mutation.isError ? <p className="text-sm text-red-300">{toErrorMessage(mutation.error)}</p> : null}
        </div>
      </form>
    </div>
  );
}
