export function ErrorState({ message }: { message: string }) {
  return (
    <div className="panel border-danger/40 bg-danger/5 p-6 text-sm text-red-200">
      {message}
    </div>
  );
}
