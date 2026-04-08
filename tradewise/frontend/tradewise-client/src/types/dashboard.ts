export type PortfolioGrowthPoint = {
  date: string;
  value: number;
};

export type DashboardAnalyticsResponse = {
  totalBalance: number;
  balanceChangePercent: number;
  activeStrategies: number;
  pausedStrategies: number;
  totalPortfolios: number;
  unreadNotifications: number;
  portfolioGrowth: PortfolioGrowthPoint[];
};
