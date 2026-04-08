import { cn } from "@/lib/utils/cn";

export function StatCard({ title, value, hint, className }: { title: string; value: string; hint?: string; className?: string }) {
  return (
    <div className={cn("panel p-5", className)}>
      <p className="text-sm text-muted">{title}</p>
      <p className="mt-3 text-3xl font-semibold text-slate-100">{value}</p>
      {hint ? <p className="mt-2 text-xs text-muted">{hint}</p> : null}
    </div>
  );
}
