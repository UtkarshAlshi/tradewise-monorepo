"use client";

import { X } from "lucide-react";
import { useUiStore } from "@/store/ui-store";
import { useNotifications } from "@/hooks/use-notifications";
import { formatDateTime } from "@/lib/utils/format";

export function NotificationsDrawer() {
  const open = useUiStore((state) => state.notificationsOpen);
  const setOpen = useUiStore((state) => state.setNotificationsOpen);
  const { items, markAllRead } = useNotifications();

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex justify-end bg-black/40">
      <div className="flex h-full w-full max-w-md flex-col border-l border-border bg-panel p-5 shadow-panel">
        <div className="mb-4 flex items-center justify-between">
          <div>
            <p className="text-lg font-semibold text-slate-100">Notifications</p>
            <p className="text-sm text-muted">Realtime strategy and price alerts.</p>
          </div>
          <button className="secondary-btn px-3" type="button" onClick={() => setOpen(false)}>
            <X className="h-4 w-4" />
          </button>
        </div>
        <div className="mb-4 flex gap-2">
          <button type="button" className="secondary-btn" onClick={() => markAllRead()}>
            Mark all read
          </button>
        </div>
        <div className="space-y-3 overflow-y-auto">
          {items.length === 0 ? (
            <div className="panel-alt p-4 text-sm text-muted">No notifications yet.</div>
          ) : (
            items.map((item, index) => (
              <div key={`${item.id ?? index}-${item.createdAt ?? index}`} className="panel-alt p-4">
                <div className="mb-2 flex items-center justify-between gap-3">
                  <span className={`inline-flex h-2.5 w-2.5 rounded-full ${item.read ? "bg-slate-600" : "bg-brand"}`} />
                  {item.createdAt ? <span className="text-xs text-muted">{formatDateTime(item.createdAt)}</span> : null}
                </div>
                <p className="text-sm text-slate-200">{item.message}</p>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
