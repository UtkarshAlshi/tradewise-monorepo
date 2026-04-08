import type { PortfolioGrowthPoint } from "@/types/dashboard";

export function PortfolioGrowthChart({ data }: { data: PortfolioGrowthPoint[] }) {
  const max = Math.max(...data.map((point) => point.value), 1);

  return (
    <div className="panel p-5">
      <div className="mb-4">
        <p className="text-lg font-semibold text-slate-100">Portfolio growth</p>
        <p className="text-sm text-muted">Current placeholder series from dashboard analytics.</p>
      </div>
      <div className="flex h-64 items-end gap-4 rounded-xl border border-border bg-slate-950/30 p-4">
        {data.map((point) => (
          <div key={point.date} className="flex flex-1 flex-col items-center gap-3">
            <div className="flex h-44 w-full items-end rounded-lg bg-slate-900/70 p-2">
              <div
                className="w-full rounded-md bg-brand"
                style={{ height: `${Math.max((point.value / max) * 100, 6)}%` }}
              />
            </div>
            <div className="text-center">
              <p className="text-xs text-muted">{point.date}</p>
              <p className="text-xs text-slate-200">{point.value.toLocaleString()}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
