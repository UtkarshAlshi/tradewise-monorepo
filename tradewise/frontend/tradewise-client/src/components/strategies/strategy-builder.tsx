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

  function validatePayload(payload: CreateStrategyRequest) {
    if (!payload.name.trim()) return "Strategy name is required";
    if (payload.rules.length === 0) return "At least one rule is required";
    if (!payload.rules.some((rule) => rule.action === "BUY")) return "At least one BUY rule is required";
    if (!payload.rules.some((rule) => rule.action === "SELL")) return "At least one SELL rule is required";
    return null;
  }

  const validationMessage = validatePayload(form);

  return (
    <div className="space-y-6">
      <div className="panel p-5">
        <div className="grid gap-4 md:grid-cols-2">
          <div>
            <label className="field-label">Strategy name</label>
            <input className="field-input" value={form.name} onChange={(e) => setForm((current) => ({ ...current, name: e.target.value }))} />
          </div>
          <div>
            <label className="field-label">Description</label>
            <input className="field-input" value={form.description ?? ""} onChange={(e) => setForm((current) => ({ ...current, description: e.target.value }))} />
          </div>
        </div>
      </div>

      <div className="flex flex-wrap gap-3">
        <button className="secondary-btn" type="button" onClick={() => addRule("BUY")}>Add BUY rule</button>
        <button className="secondary-btn" type="button" onClick={() => addRule("SELL")}>Add SELL rule</button>
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
              onChange={(next) => updateRule(ruleIndex, {
                ...rule,
                conditions: rule.conditions.map((currentCondition, index) => (index === conditionIndex ? next : currentCondition)),
              })}
              onRemove={() => updateRule(ruleIndex, {
                ...rule,
                conditions: rule.conditions.filter((_, index) => index !== conditionIndex),
              })}
            />
          ))}

          <button className="secondary-btn" type="button" onClick={() => addCondition(ruleIndex)}>Add condition</button>
        </RuleCard>
      ))}

      {validationMessage ? <p className="text-sm text-red-300">{validationMessage}</p> : null}
      {mutation.isError ? <p className="text-sm text-red-300">{toErrorMessage(mutation.error)}</p> : null}

      <div className="flex gap-3">
        <button
          className="primary-btn"
          type="button"
          disabled={Boolean(validationMessage) || mutation.isPending}
          onClick={() => mutation.mutate(form, {
            onSuccess: () => router.push("/strategies"),
          })}
        >
          {mutation.isPending ? "Saving strategy..." : "Save strategy"}
        </button>
      </div>
    </div>
  );
}
