import type { LeaderboardEntryResponse } from "@/types/leaderboard";
import { formatPercent } from "@/lib/utils/format";

export function LeaderboardTable({ entries }: { entries: LeaderboardEntryResponse[] }) {
  return (
    <div className="panel overflow-hidden">
      <table className="min-w-full divide-y divide-border text-sm">
        <thead className="bg-slate-950/30 text-left text-muted">
          <tr>
            <th className="px-4 py-3">Rank</th>
            <th className="px-4 py-3">Portfolio</th>
            <th className="px-4 py-3">User</th>
            <th className="px-4 py-3">Return</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-border">
          {entries.map((entry) => (
            <tr key={`${entry.rank}-${entry.portfolioName}`}>
              <td className="px-4 py-3 font-semibold text-slate-100">#{entry.rank}</td>
              <td className="px-4 py-3 text-slate-200">{entry.portfolioName}</td>
              <td className="px-4 py-3 text-muted">{entry.userEmail}</td>
              <td className={`px-4 py-3 font-medium ${entry.totalReturnPercent >= 0 ? "text-emerald-400" : "text-red-400"}`}>{formatPercent(entry.totalReturnPercent)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
