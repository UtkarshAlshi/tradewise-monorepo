import { formatCurrency } from "@/lib/utils/format";

export function LivePriceChip({ symbol, price }: { symbol: string; price?: number }) {
  return (
    <div className="rounded-full border border-border bg-slate-900/60 px-3 py-2 text-xs text-slate-200">
      <span className="mr-2 font-semibold">{symbol}</span>
      <span className="text-muted">{price !== undefined ? formatCurrency(price) : "Live pending"}</span>
    </div>
  );
}
