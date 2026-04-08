"use client";

import { Trash2 } from "lucide-react";
import type { StrategyCondition } from "@/types/strategy";

type Props = {
  condition: StrategyCondition;
  onChange: (next: StrategyCondition) => void;
  onRemove: () => void;
};

const indicators = ["PRICE", "SMA", "EMA", "RSI"] as const;
const operators = ["GREATER_THAN", "LESS_THAN", "CROSSES_ABOVE", "CROSSES_BELOW"] as const;
const indicatorBTypes = ["VALUE", "INDICATOR"] as const;

export function ConditionRow({ condition, onChange, onRemove }: Props) {
  const needsPeriodA = condition.indicatorA !== "PRICE";
  const needsPeriodB = condition.indicatorBType === "INDICATOR" && condition.indicatorBValue !== "PRICE";

  return (
    <div className="rounded-xl border border-border bg-slate-950/30 p-4">
      <div className="mb-4 flex items-center justify-between">
        <p className="text-sm font-semibold text-slate-100">Condition</p>
        <button className="secondary-btn px-3" type="button" onClick={onRemove}>
          <Trash2 className="h-4 w-4" />
        </button>
      </div>
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <div>
          <label className="field-label">Indicator A</label>
          <select className="field-input" value={condition.indicatorA} onChange={(e) => onChange({ ...condition, indicatorA: e.target.value as StrategyCondition["indicatorA"] })}>
            {indicators.map((indicator) => <option key={indicator} value={indicator}>{indicator}</option>)}
          </select>
        </div>
        <div>
          <label className="field-label">Indicator A period</label>
          <input
            className="field-input"
            value={condition.indicatorAParams.period ?? ""}
            disabled={!needsPeriodA}
            onChange={(e) => onChange({
              ...condition,
              indicatorAParams: needsPeriodA ? { ...condition.indicatorAParams, period: e.target.value } : {},
            })}
          />
        </div>
        <div>
          <label className="field-label">Operator</label>
          <select className="field-input" value={condition.operator} onChange={(e) => onChange({ ...condition, operator: e.target.value as StrategyCondition["operator"] })}>
            {operators.map((operator) => <option key={operator} value={operator}>{operator}</option>)}
          </select>
        </div>
        <div>
          <label className="field-label">Indicator B type</label>
          <select className="field-input" value={condition.indicatorBType} onChange={(e) => onChange({ ...condition, indicatorBType: e.target.value as StrategyCondition["indicatorBType"] })}>
            {indicatorBTypes.map((type) => <option key={type} value={type}>{type}</option>)}
          </select>
        </div>
        <div>
          <label className="field-label">Indicator B value</label>
          {condition.indicatorBType === "VALUE" ? (
            <input className="field-input" value={condition.indicatorBValue} onChange={(e) => onChange({ ...condition, indicatorBValue: e.target.value, indicatorBParams: {} })} />
          ) : (
            <select className="field-input" value={condition.indicatorBValue} onChange={(e) => onChange({ ...condition, indicatorBValue: e.target.value, indicatorBParams: e.target.value === "PRICE" ? {} : condition.indicatorBParams })}>
              {indicators.map((indicator) => <option key={indicator} value={indicator}>{indicator}</option>)}
            </select>
          )}
        </div>
        <div>
          <label className="field-label">Indicator B period</label>
          <input
            className="field-input"
            value={condition.indicatorBParams.period ?? ""}
            disabled={!needsPeriodB}
            onChange={(e) => onChange({
              ...condition,
              indicatorBParams: needsPeriodB ? { ...condition.indicatorBParams, period: e.target.value } : {},
            })}
          />
        </div>
      </div>
    </div>
  );
}
