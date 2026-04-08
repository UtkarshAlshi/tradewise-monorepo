"use client";

import { useMemo, useState } from "react";

export function useLivePrices(initialSymbols: string[]) {
  const [prices, setPrices] = useState<Record<string, number>>({});

  const trackedSymbols = useMemo(
    () => Array.from(new Set(initialSymbols.map((symbol) => symbol.toUpperCase()))),
    [initialSymbols],
  );

  function handlePriceMessage(symbol: string, payload: unknown) {
    const candidate = payload as { symbol?: string; price?: number | string };
    if (!candidate?.symbol || candidate.price === undefined) return;
    setPrices((current) => ({ ...current, [symbol]: Number(candidate.price) }));
  }

  return { trackedSymbols, prices, handlePriceMessage };
}
