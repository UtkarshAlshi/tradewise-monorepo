"use client";

import { Trash2 } from "lucide-react";
import type { PortfolioAssetResponse } from "@/types/portfolio";
import { formatCurrency, formatDateTime } from "@/lib/utils/format";

export function AssetTable({
  assets,
  livePrices,
  onDelete,
  deleting,
}: {
  assets: PortfolioAssetResponse[];
  livePrices?: Record<string, number>;
  onDelete: (assetId: string) => void;
  deleting?: boolean;
}) {
  return (
    <div className="panel overflow-hidden">
      <table className="min-w-full divide-y divide-border text-sm">
        <thead className="bg-slate-950/30 text-left text-muted">
          <tr>
            <th className="px-4 py-3">Symbol</th>
            <th className="px-4 py-3">Quantity</th>
            <th className="px-4 py-3">Purchase Price</th>
            <th className="px-4 py-3">Live Price</th>
            <th className="px-4 py-3">Purchase Date</th>
            <th className="px-4 py-3">Action</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-border">
          {assets.map((asset) => (
            <tr key={asset.id} className="text-slate-200">
              <td className="px-4 py-3 font-medium">{asset.symbol}</td>
              <td className="px-4 py-3">{asset.quantity}</td>
              <td className="px-4 py-3">{formatCurrency(asset.purchasePrice)}</td>
              <td className="px-4 py-3">{asset.symbol in (livePrices ?? {}) ? formatCurrency(livePrices?.[asset.symbol] ?? 0) : "—"}</td>
              <td className="px-4 py-3">{formatDateTime(asset.purchaseDate)}</td>
              <td className="px-4 py-3">
                <button className="secondary-btn px-3" type="button" onClick={() => onDelete(asset.id)} disabled={deleting}>
                  <Trash2 className="h-4 w-4" />
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
