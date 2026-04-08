import { Trash2 } from "lucide-react";

export function RuleCard({
  title,
  onRemove,
  children,
}: {
  title: string;
  onRemove: () => void;
  children: React.ReactNode;
}) {
  return (
    <div className="panel p-5">
      <div className="mb-4 flex items-center justify-between">
        <p className="text-lg font-semibold text-slate-100">{title}</p>
        <button className="secondary-btn px-3" type="button" onClick={onRemove}>
          <Trash2 className="h-4 w-4" />
        </button>
      </div>
      <div className="space-y-4">{children}</div>
    </div>
  );
}
