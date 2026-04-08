export function EmptyState({ title, description }: { title: string; description: string }) {
  return (
    <div className="panel flex min-h-40 flex-col items-center justify-center gap-2 px-6 text-center">
      <p className="text-lg font-semibold text-slate-100">{title}</p>
      <p className="max-w-md text-sm text-muted">{description}</p>
    </div>
  );
}
