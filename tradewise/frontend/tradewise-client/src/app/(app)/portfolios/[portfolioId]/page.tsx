"use client";

import { useMemo } from "react";
import { useParams } from "next/navigation";
import { PageContainer } from "@/components/common/page-container";
import { LoadingState } from "@/components/common/loading-state";
import { ErrorState } from "@/components/common/error-state";
import { EmptyState } from "@/components/common/empty-state";
import { AddAssetDialog } from "@/components/portfolios/add-asset-dialog";
import { AssetTable } from "@/components/portfolios/asset-table";
import { useDeleteAsset, usePortfolioAssets } from "@/hooks/use-portfolio-assets";
import { usePortfolios } from "@/hooks/use-portfolios";
import { useLivePrices } from "@/hooks/use-live-prices";
import { WebsocketProvider } from "@/providers/websocket-provider";

export default function PortfolioDetailPage() {
  const params = useParams<{ portfolioId: string }>();
  const portfolioId = params.portfolioId;

  const portfolios = usePortfolios();
  const assets = usePortfolioAssets(portfolioId);
  const deleteAsset = useDeleteAsset(portfolioId);

  const portfolio = useMemo(
    () => portfolios.data?.find((item) => item.id === portfolioId),
    [portfolioId, portfolios.data],
  );

  const trackedSymbols = useMemo(
    () => assets.data?.map((asset) => asset.symbol) ?? [],
    [assets.data],
  );
  const live = useLivePrices(trackedSymbols);

  if (portfolios.isLoading || assets.isLoading) return <PageContainer><LoadingState label="Loading portfolio..." /></PageContainer>;
  if (portfolios.isError || assets.isError) return <PageContainer><ErrorState message="Failed to load portfolio details." /></PageContainer>;
  if (!portfolio) return <PageContainer><ErrorState message="Portfolio not found." /></PageContainer>;

  return (
    <WebsocketProvider trackedSymbols={live.trackedSymbols} onPriceMessage={live.handlePriceMessage}>
      <PageContainer className="space-y-6">
        <div className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div>
            <h1 className="page-title">{portfolio.name}</h1>
            <p className="page-subtitle">{portfolio.description || "No description provided."}</p>
          </div>
          <AddAssetDialog portfolioId={portfolioId} />
        </div>

        {assets.data && assets.data.length > 0 ? (
          <AssetTable
            assets={assets.data}
            livePrices={live.prices}
            onDelete={(assetId) => deleteAsset.mutate(assetId)}
            deleting={deleteAsset.isPending}
          />
        ) : (
          <EmptyState title="No assets yet" description="Add an asset to begin tracking this portfolio." />
        )}
      </PageContainer>
    </WebsocketProvider>
  );
}
