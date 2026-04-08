import { cn } from "@/lib/utils/cn";

export function PageContainer({ className, children }: { className?: string; children: React.ReactNode }) {
  return <div className={cn("mx-auto w-full max-w-7xl p-6", className)}>{children}</div>;
}
