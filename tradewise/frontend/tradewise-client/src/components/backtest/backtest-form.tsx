"use client";

import { useMemo } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useStrategies } from "@/hooks/use-strategies";
import { useRunBacktest } from "@/hooks/use-backtest";
import { BacktestReportCard } from "@/components/backtest/backtest-report-card";
import { LoadingState } from "@/components/common/loading-state";
import { ErrorState } from "@/components/common/error-state";
import { toErrorMessage } from "@/lib/utils/errors";

const schema = z.object({
  strategyId: z.string().min(1),
  symbol: z.string().min(1),
  startDate: z.string().min(1),
  endDate: z.string().min(1),
  initialCash: z.coerce.number().positive(),
});

type FormValues = z.infer<typeof schema>;

export function BacktestForm() {
  const { data: strategies, isLoading, isError } = useStrategies();
  const mutation = useRunBacktest();

  const defaultStrategyId = useMemo(() => strategies?.[0]?.id ?? "", [strategies]);

  const { register, handleSubmit, setValue, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    values: {
      strategyId: defaultStrategyId,
      symbol: "IBM",
      startDate: "2024-01-01",
      endDate: "2024-06-30",
      initialCash: 10000,
    },
  });

  if (isLoading) return <LoadingState label="Loading strategies..." />;
  if (isError) return <ErrorState message="Failed to load strategies." />;

  return (
    <div className="grid gap-6 xl:grid-cols-[420px_1fr]">
      <form className="panel p-5" onSubmit={handleSubmit((values) => mutation.mutate(values))}>
        <div className="mb-6">
          <p className="text-xl font-semibold text-slate-100">Run backtest</p>
          <p className="text-sm text-muted">Test a saved strategy against historical market data.</p>
        </div>
        <div className="space-y-4">
          <div>
            <label className="field-label">Strategy</label>
            <select className="field-input" {...register("strategyId")} onChange={(e) => setValue("strategyId", e.target.value)}>
              <option value="">Select a strategy</option>
              {strategies?.map((strategy) => (
                <option key={strategy.id} value={strategy.id}>{strategy.name}</option>
              ))}
            </select>
            {errors.strategyId ? <p className="mt-1 text-xs text-red-300">{errors.strategyId.message}</p> : null}
          </div>
          <div>
            <label className="field-label">Symbol</label>
            <input className="field-input" {...register("symbol")} />
          </div>
          <div className="grid gap-4 md:grid-cols-2">
            <div>
              <label className="field-label">Start date</label>
              <input className="field-input" type="date" {...register("startDate")} />
            </div>
            <div>
              <label className="field-label">End date</label>
              <input className="field-input" type="date" {...register("endDate")} />
            </div>
          </div>
          <div>
            <label className="field-label">Initial cash</label>
            <input className="field-input" type="number" step="any" {...register("initialCash")} />
          </div>
          {mutation.isError ? <p className="text-sm text-red-300">{toErrorMessage(mutation.error)}</p> : null}
          <button className="primary-btn w-full" type="submit" disabled={mutation.isPending}>
            {mutation.isPending ? "Running backtest..." : "Run backtest"}
          </button>
        </div>
      </form>
      <BacktestReportCard report={mutation.data ?? null} />
    </div>
  );
}
