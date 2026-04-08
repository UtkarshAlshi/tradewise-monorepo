import Link from "next/link";
import { ArrowRight } from "lucide-react";
import type { PortfolioResponse } from "@/types/portfolio";
import { formatDateTime } from "@/lib/utils/format";

export function PortfolioCard({ portfolio }: { portfolio: PortfolioResponse }) {
  return (
    <div className="panel p-5">
      <div className="mb-4 flex items-start justify-between gap-4">
        <div>
          <p className="text-lg font-semibold text-slate-100">{portfolio.name}</p>
          <p className="mt-1 text-sm text-muted">{portfolio.description || "No description provided."}</p>
        </div>
        <span className="rounded-full border border-border px-3 py-1 text-xs text-muted">{formatDateTime(portfolio.createdAt)}</span>
      </div>
      <Link href={`/portfolios/${portfolio.id}`} className="secondary-btn">
        Open portfolio
        <ArrowRight className="ml-2 h-4 w-4" />
      </Link>
    </div>
  );
}
