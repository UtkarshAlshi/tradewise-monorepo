import { cn } from "@/lib/utils/cn";

export function MetricCard({ title, value, tone = "neutral" }: { title: string; value: string; tone?: "positive" | "negative" | "neutral" }) {
  return (
    <div className="panel-alt p-4">
      <p className="text-sm text-muted">{title}</p>
      <p className={cn(
        "mt-3 text-2xl font-semibold",
        tone === "positive" && "text-emerald-400",
        tone === "negative" && "text-red-400",
        tone === "neutral" && "text-slate-100",
      )}>{value}</p>
    </div>
  );
}
