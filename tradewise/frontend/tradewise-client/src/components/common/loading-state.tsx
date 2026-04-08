export function LoadingState({ label = "Loading..." }: { label?: string }) {
  return (
    <div className="panel flex min-h-32 items-center justify-center text-sm text-muted">
      {label}
    </div>
  );
}
