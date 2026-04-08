"use client";

import { PageContainer } from "@/components/common/page-container";
import { LoadingState } from "@/components/common/loading-state";
import { ErrorState } from "@/components/common/error-state";
import { StatCard } from "@/components/dashboard/stat-card";
import { PortfolioGrowthChart } from "@/components/dashboard/portfolio-growth-chart";
import { LivePriceChip } from "@/components/prices/live-price-chip";
import { useDashboard } from "@/hooks/use-dashboard";
import { useLeaderboard } from "@/hooks/use-leaderboard";

export default function DashboardPage() {
  const dashboard = useDashboard();
  const leaderboard = useLeaderboard();

  if (dashboard.isLoading) return <PageContainer><LoadingState label="Loading dashboard..." /></PageContainer>;
  if (dashboard.isError || !dashboard.data) return <PageContainer><ErrorState message="Unable to load dashboard analytics." /></PageContainer>;

  return (
    <PageContainer className="space-y-6">
      <div>
        <h1 className="page-title">Dashboard</h1>
        <p className="page-subtitle">Your central workspace for portfolios, strategies, and backtests.</p>
      </div>

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <StatCard title="Total Balance" value={`$${dashboard.data.totalBalance.toFixed(2)}`} hint={`${dashboard.data.balanceChangePercent.toFixed(2)}% change`} />
        <StatCard title="Active Strategies" value={String(dashboard.data.activeStrategies)} />
        <StatCard title="Total Portfolios" value={String(dashboard.data.totalPortfolios)} />
        <StatCard title="Unread Notifications" value={String(dashboard.data.unreadNotifications)} />
      </div>

      <div className="grid gap-6 xl:grid-cols-[1.4fr_0.8fr]">
        <PortfolioGrowthChart data={dashboard.data.portfolioGrowth} />
        <div className="space-y-6">
          <div className="panel p-5">
            <p className="mb-4 text-lg font-semibold text-slate-100">Live watchlist</p>
            <div className="flex flex-wrap gap-3">
              <LivePriceChip symbol="IBM" />
              <LivePriceChip symbol="AAPL" />
              <LivePriceChip symbol="MSFT" />
            </div>
          </div>
          <div className="panel p-5">
            <p className="mb-4 text-lg font-semibold text-slate-100">Leaderboard preview</p>
            <div className="space-y-3">
              {leaderboard.data?.slice(0, 5).map((entry) => (
                <div key={`${entry.rank}-${entry.portfolioName}`} className="flex items-center justify-between rounded-xl border border-border bg-slate-950/30 px-4 py-3">
                  <div>
                    <p className="font-medium text-slate-100">#{entry.rank} {entry.portfolioName}</p>
                    <p className="text-xs text-muted">{entry.userEmail}</p>
                  </div>
                  <p className={entry.totalReturnPercent >= 0 ? "text-emerald-400" : "text-red-400"}>{entry.totalReturnPercent.toFixed(2)}%</p>
                </div>
              )) ?? <p className="text-sm text-muted">Leaderboard data will appear here.</p>}
            </div>
          </div>
        </div>
      </div>
    </PageContainer>
  );
}
