export type PortfolioResponse = {
  id: string;
  name: string;
  description?: string;
  createdAt: string;
  userEmail: string;
};

export type CreatePortfolioRequest = {
  name: string;
  description?: string;
};

export type PortfolioAssetResponse = {
  id: string;
  symbol: string;
  quantity: number;
  purchasePrice: number;
  purchaseDate: string;
  portfolioId: string;
};

export type AddAssetRequest = {
  symbol: string;
  quantity: number;
  purchasePrice: number;
  purchaseDate: string;
};
