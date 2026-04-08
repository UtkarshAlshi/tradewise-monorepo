import { PageContainer } from "@/components/common/page-container";
import { BacktestForm } from "@/components/backtest/backtest-form";

export default function BacktestPage() {
  return (
    <PageContainer className="space-y-6">
      <div>
        <h1 className="page-title">Backtest</h1>
        <p className="page-subtitle">Run a strategy against historical data and inspect the performance report.</p>
      </div>
      <BacktestForm />
    </PageContainer>
  );
}
