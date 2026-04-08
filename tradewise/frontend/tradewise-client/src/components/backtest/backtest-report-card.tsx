import { MetricCard } from "@/components/backtest/metric-card";
import { EmptyState } from "@/components/common/empty-state";
import type { BacktestReportResponse } from "@/types/backtest";
import { formatCurrency, formatPercent } from "@/lib/utils/format";

export function BacktestReportCard({ report }: { report: BacktestReportResponse | null }) {
  if (!report) {
    return <EmptyState title="No backtest run yet" description="Choose a strategy and run a backtest to see performance metrics." />;
  }

  const pnl = Number(report.totalProfitLoss);

  return (
    <div className="panel p-5">
      <div className="mb-6">
        <p className="text-xl font-semibold text-slate-100">Backtest report</p>
        <p className="text-sm text-muted">{report.strategyName} · {report.symbol}</p>
      </div>
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
        <MetricCard title="Total Trades" value={String(report.totalTrades)} />
        <MetricCard title="Profit / Loss" value={formatCurrency(report.totalProfitLoss)} tone={pnl > 0 ? "positive" : pnl < 0 ? "negative" : "neutral"} />
        <MetricCard title="Return" value={formatPercent(report.totalReturnPercent)} tone={Number(report.totalReturnPercent) > 0 ? "positive" : Number(report.totalReturnPercent) < 0 ? "negative" : "neutral"} />
        <MetricCard title="Win Rate" value={formatPercent(report.winRatePercent)} />
        <MetricCard title="Max Drawdown" value={formatPercent(report.maxDrawdownPercent)} tone={Number(report.maxDrawdownPercent) > 0 ? "negative" : "neutral"} />
      </div>
    </div>
  );
}
