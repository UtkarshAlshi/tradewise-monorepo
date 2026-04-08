import { apiFetch } from "@/lib/api/client";
import type {
  AddAssetRequest,
  CreatePortfolioRequest,
  PortfolioAssetResponse,
  PortfolioResponse,
} from "@/types/portfolio";

export function getPortfolios(token: string) {
  return apiFetch<PortfolioResponse[]>("/api/portfolios", {}, token);
}

export function createPortfolio(payload: CreatePortfolioRequest, token: string) {
  return apiFetch<PortfolioResponse>("/api/portfolios", {
    method: "POST",
    body: JSON.stringify(payload),
  }, token);
}

export function getPortfolioAssets(portfolioId: string, token: string) {
  return apiFetch<PortfolioAssetResponse[]>(`/api/portfolios/${portfolioId}/assets`, {}, token);
}

export function addAsset(portfolioId: string, payload: AddAssetRequest, token: string) {
  return apiFetch<PortfolioAssetResponse>(`/api/portfolios/${portfolioId}/assets`, {
    method: "POST",
    body: JSON.stringify(payload),
  }, token);
}

export function deleteAsset(portfolioId: string, assetId: string, token: string) {
  return apiFetch<void>(`/api/portfolios/${portfolioId}/assets/${assetId}`, {
    method: "DELETE",
  }, token);
}
