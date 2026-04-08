import { PageContainer } from "@/components/common/page-container";
import { StrategyBuilder } from "@/components/strategies/strategy-builder";

export default function NewStrategyPage() {
  return (
    <PageContainer className="space-y-6">
      <div>
        <h1 className="page-title">Create strategy</h1>
        <p className="page-subtitle">Define BUY and SELL rule groups using technical indicators and thresholds.</p>
      </div>
      <StrategyBuilder />
    </PageContainer>
  );
}
