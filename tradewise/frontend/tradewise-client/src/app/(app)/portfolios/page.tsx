"use client";

import { PageContainer } from "@/components/common/page-container";
import { LoadingState } from "@/components/common/loading-state";
import { ErrorState } from "@/components/common/error-state";
import { EmptyState } from "@/components/common/empty-state";
import { CreatePortfolioDialog } from "@/components/portfolios/create-portfolio-dialog";
import { PortfolioCard } from "@/components/portfolios/portfolio-card";
import { usePortfolios } from "@/hooks/use-portfolios";

export default function PortfoliosPage() {
  const portfolios = usePortfolios();

  return (
    <PageContainer className="space-y-6">
      <div className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="page-title">Portfolios</h1>
          <p className="page-subtitle">Create and manage asset baskets with live price visibility.</p>
        </div>
        <CreatePortfolioDialog />
      </div>

      {portfolios.isLoading ? <LoadingState label="Loading portfolios..." /> : null}
      {portfolios.isError ? <ErrorState message="Failed to load portfolios." /> : null}
      {portfolios.data && portfolios.data.length === 0 ? (
        <EmptyState title="No portfolios yet" description="Create your first portfolio to start tracking assets." />
      ) : null}
      {portfolios.data && portfolios.data.length > 0 ? (
        <div className="grid gap-4 xl:grid-cols-2">
          {portfolios.data.map((portfolio) => <PortfolioCard key={portfolio.id} portfolio={portfolio} />)}
        </div>
      ) : null}
    </PageContainer>
  );
}
