export type StrategyCondition = {
  id?: number;
  indicatorA: "PRICE" | "SMA" | "EMA" | "RSI";
  indicatorAParams: Record<string, string>;
  operator: "GREATER_THAN" | "LESS_THAN" | "CROSSES_ABOVE" | "CROSSES_BELOW";
  indicatorBType: "VALUE" | "INDICATOR";
  indicatorBValue: string;
  indicatorBParams: Record<string, string>;
};

export type StrategyRule = {
  id?: number;
  action: "BUY" | "SELL";
  conditions: StrategyCondition[];
};

export type CreateStrategyRequest = {
  name: string;
  description?: string;
  rules: StrategyRule[];
};

export type StrategyResponse = {
  id: string;
  name: string;
  description?: string;
  createdAt: string;
  userEmail: string;
};
