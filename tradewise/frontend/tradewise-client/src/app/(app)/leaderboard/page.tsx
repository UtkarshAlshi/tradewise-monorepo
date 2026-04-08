"use client";

import { PageContainer } from "@/components/common/page-container";
import { LoadingState } from "@/components/common/loading-state";
import { ErrorState } from "@/components/common/error-state";
import { EmptyState } from "@/components/common/empty-state";
import { LeaderboardTable } from "@/components/leaderboard/leaderboard-table";
import { useLeaderboard } from "@/hooks/use-leaderboard";

export default function LeaderboardPage() {
  const leaderboard = useLeaderboard();

  return (
    <PageContainer className="space-y-6">
      <div>
        <h1 className="page-title">Leaderboard</h1>
        <p className="page-subtitle">See the top-performing portfolios across the platform.</p>
      </div>
      {leaderboard.isLoading ? <LoadingState label="Loading leaderboard..." /> : null}
      {leaderboard.isError ? <ErrorState message="Failed to load leaderboard." /> : null}
      {leaderboard.data && leaderboard.data.length === 0 ? (
        <EmptyState title="No leaderboard data yet" description="Once portfolios are ranked, they will appear here." />
      ) : null}
      {leaderboard.data && leaderboard.data.length > 0 ? <LeaderboardTable entries={leaderboard.data} /> : null}
    </PageContainer>
  );
}
