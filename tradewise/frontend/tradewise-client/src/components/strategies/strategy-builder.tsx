"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import type { CreateStrategyRequest, StrategyCondition, StrategyRule } from "@/types/strategy";
import { useCreateStrategy } from "@/hooks/use-strategies";
import { RuleCard } from "@/components/strategies/rule-card";
import { ConditionRow } from "@/components/strategies/condition-row";
import { toErrorMessage } from "@/lib/utils/errors";

function createDefaultCondition(): StrategyCondition {
  return {
    indicatorA: "RSI",
    indicatorAParams: { period: "14" },
    operator: "LESS_THAN",
    indicatorBType: "VALUE",
    indicatorBValue: "30",
    indicatorBParams: {},
  };
}

function createDefaultRule(action: "BUY" | "SELL"): StrategyRule {
  return {
    action,
    conditions: [createDefaultCondition()],
  };
}

function needsPeriod(indicator: string) {
  return indicator === "SMA" || indicator === "EMA" || indicator === "RSI";
}

function normalizeCondition(condition: StrategyCondition): StrategyCondition {
  const indicatorA = condition.indicatorA;
  const indicatorBType = condition.indicatorBType;

  const normalized: StrategyCondition = {
    indicatorA,
    indicatorAParams: needsPeriod(indicatorA)
      ? { period: condition.indicatorAParams?.period?.trim() || "14" }
      : {},
    operator: condition.operator,
    indicatorBType,
    indicatorBValue: condition.indicatorBValue?.trim() || "",
    indicatorBParams: {},
  };

  if (indicatorBType === "VALUE") {
    normalized.indicatorBValue = condition.indicatorBValue?.trim() || "";
    normalized.indicatorBParams = {};
    return normalized;
  }

  // INDICATOR mode
  const indicatorB = condition.indicatorBValue?.trim() || "PRICE";
  normalized.indicatorBValue = indicatorB;
  normalized.indicatorBParams = needsPeriod(indicatorB)
    ? { period: condition.indicatorBParams?.period?.trim() || "14" }
    : {};

  return normalized;
}

function normalizePayload(payload: CreateStrategyRequest): CreateStrategyRequest {
  return {
    name: payload.name.trim(),
    description: payload.description?.trim() || "",
    rules: payload.rules
      .map((rule) => ({
        action: rule.action,
        conditions: rule.conditions
          .map(normalizeCondition)
          .filter((condition) => {
            if (!condition.indicatorA) return false;
            if (!condition.operator) return false;
            if (!condition.indicatorBType) return false;
            if (!condition.indicatorBValue) return false;
            return true;
          }),
      }))
      .filter((rule) => rule.conditions.length > 0),
  };
}

function validatePayload(payload: CreateStrategyRequest) {
  if (!payload.name.trim()) return "Strategy name is required";
  if (payload.rules.length === 0) return "At least one rule is required";
  if (!payload.rules.some((rule) => rule.action === "BUY")) return "At least one BUY rule is required";
  if (!payload.rules.some((rule) => rule.action === "SELL")) return "At least one SELL rule is required";

  for (const rule of payload.rules) {
    if (!rule.conditions.length) return `Rule ${rule.action} must contain at least one condition`;

    for (const condition of rule.conditions) {
      if (!condition.indicatorA) return "Indicator A is required";
      if (!condition.operator) return "Operator is required";
      if (!condition.indicatorBType) return "Indicator B type is required";
      if (!condition.indicatorBValue) return "Indicator B value is required";

      if (needsPeriod(condition.indicatorA) && !condition.indicatorAParams?.period?.trim()) {
        return `Indicator A period is required for ${condition.indicatorA}`;
      }

      if (
        condition.indicatorBType === "INDICATOR" &&
        needsPeriod(condition.indicatorBValue) &&
        !condition.indicatorBParams?.period?.trim()
      ) {
        return `Indicator B period is required for ${condition.indicatorBValue}`;
      }
    }
  }

  return null;
}

export function StrategyBuilder() {
  const router = useRouter();
  const mutation = useCreateStrategy();
  const [form, setForm] = useState<CreateStrategyRequest>({
    name: "",
    description: "",
    rules: [createDefaultRule("BUY"), createDefaultRule("SELL")],
  });

  function updateRule(ruleIndex: number, next: StrategyRule) {
    setForm((current) => ({
      ...current,
      rules: current.rules.map((rule, index) => (index === ruleIndex ? next : rule)),
    }));
  }

  function removeRule(ruleIndex: number) {
    setForm((current) => ({
      ...current,
      rules: current.rules.filter((_, index) => index !== ruleIndex),
    }));
  }

  function addRule(action: "BUY" | "SELL") {
    setForm((current) => ({
      ...current,
      rules: [...current.rules, createDefaultRule(action)],
    }));
  }

  function addCondition(ruleIndex: number) {
    setForm((current) => ({
      ...current,
      rules: current.rules.map((rule, index) =>
        index === ruleIndex ? { ...rule, conditions: [...rule.conditions, createDefaultCondition()] } : rule,
      ),
    }));
  }

  const normalizedForm = normalizePayload(form);
  const validationMessage = validatePayload(normalizedForm);

  return (
    <div className="space-y-6">
      <div className="panel p-5">
        <div className="grid gap-4 md:grid-cols-2">
          <div>
            <label className="field-label">Strategy name</label>
            <input
              className="field-input"
              value={form.name}
              onChange={(e) => setForm((current) => ({ ...current, name: e.target.value }))}
            />
          </div>
          <div>
            <label className="field-label">Description</label>
            <input
              className="field-input"
              value={form.description ?? ""}
              onChange={(e) => setForm((current) => ({ ...current, description: e.target.value }))}
            />
          </div>
        </div>
      </div>

      <div className="flex flex-wrap gap-3">
        <button className="secondary-btn" type="button" onClick={() => addRule("BUY")}>
          Add BUY rule
        </button>
        <button className="secondary-btn" type="button" onClick={() => addRule("SELL")}>
          Add SELL rule
        </button>
      </div>

      {form.rules.map((rule, ruleIndex) => (
        <RuleCard key={`${rule.action}-${ruleIndex}`} title={`Rule ${ruleIndex + 1} · ${rule.action}`} onRemove={() => removeRule(ruleIndex)}>
          <div>
            <label className="field-label">Rule action</label>
            <select
              className="field-input max-w-xs"
              value={rule.action}
              onChange={(e) => updateRule(ruleIndex, { ...rule, action: e.target.value as StrategyRule["action"] })}
            >
              <option value="BUY">BUY</option>
              <option value="SELL">SELL</option>
            </select>
          </div>

          {rule.conditions.map((condition, conditionIndex) => (
            <ConditionRow
              key={`${ruleIndex}-${conditionIndex}`}
              condition={condition}
              onChange={(next) =>
                updateRule(ruleIndex, {
                  ...rule,
                  conditions: rule.conditions.map((currentCondition, index) =>
                    index === conditionIndex ? next : currentCondition,
                  ),
                })
              }
              onRemove={() =>
                updateRule(ruleIndex, {
                  ...rule,
                  conditions: rule.conditions.filter((_, index) => index !== conditionIndex),
                })
              }
            />
          ))}

          <button className="secondary-btn" type="button" onClick={() => addCondition(ruleIndex)}>
            Add condition
          </button>
        </RuleCard>
      ))}

      {validationMessage ? <p className="text-sm text-red-300">{validationMessage}</p> : null}
      {mutation.isError ? <p className="text-sm text-red-300">{toErrorMessage(mutation.error)}</p> : null}

      <div className="flex gap-3">
        <button
          className="primary-btn"
          type="button"
          disabled={Boolean(validationMessage) || mutation.isPending}
          onClick={() =>
            mutation.mutate(normalizedForm, {
              onSuccess: () => router.push("/strategies"),
            })
          }
        >
          {mutation.isPending ? "Saving strategy..." : "Save strategy"}
        </button>
      </div>
    </div>
  );
}