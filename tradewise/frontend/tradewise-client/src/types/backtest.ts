export type BacktestRequest = {
  strategyId: string;
  symbol: string;
  startDate: string;
  endDate: string;
  initialCash: number;
};

export type BacktestReportResponse = {
  strategyName: string;
  symbol: string;
  totalTrades: number;
  totalProfitLoss: number;
  totalReturnPercent: number;
  winRatePercent: number;
  maxDrawdownPercent: number;
};
