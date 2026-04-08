"use client";

import Link from "next/link";
import { PageContainer } from "@/components/common/page-container";
import { LoadingState } from "@/components/common/loading-state";
import { ErrorState } from "@/components/common/error-state";
import { EmptyState } from "@/components/common/empty-state";
import { useStrategies } from "@/hooks/use-strategies";
import { formatDateTime } from "@/lib/utils/format";

export default function StrategiesPage() {
  const strategies = useStrategies();

  return (
    <PageContainer className="space-y-6">
      <div className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="page-title">Strategies</h1>
          <p className="page-subtitle">Create and save buy/sell rule sets for backtesting.</p>
        </div>
        <Link href="/strategies/new" className="primary-btn">Create strategy</Link>
      </div>

      {strategies.isLoading ? <LoadingState label="Loading strategies..." /> : null}
      {strategies.isError ? <ErrorState message="Failed to load strategies." /> : null}
      {strategies.data && strategies.data.length === 0 ? (
        <EmptyState title="No strategies yet" description="Build your first rule-based strategy." />
      ) : null}
      {strategies.data && strategies.data.length > 0 ? (
        <div className="grid gap-4 xl:grid-cols-2">
          {strategies.data.map((strategy) => (
            <div key={strategy.id} className="panel p-5">
              <div className="mb-3 flex items-start justify-between gap-4">
                <div>
                  <p className="text-lg font-semibold text-slate-100">{strategy.name}</p>
                  <p className="mt-1 text-sm text-muted">{strategy.description || "No description provided."}</p>
                </div>
                <span className="rounded-full border border-border px-3 py-1 text-xs text-muted">{formatDateTime(strategy.createdAt)}</span>
              </div>
              <div className="text-xs text-muted">Owner: {strategy.userEmail}</div>
            </div>
          ))}
        </div>
      ) : null}
    </PageContainer>
  );
}
