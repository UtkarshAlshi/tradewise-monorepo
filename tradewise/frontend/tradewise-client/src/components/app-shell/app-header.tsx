"use client";

import { Bell, LogOut } from "lucide-react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/auth-store";
import { useUiStore } from "@/store/ui-store";
import { useNotifications } from "@/hooks/use-notifications";

export function AppHeader() {
  const router = useRouter();
  const clearSession = useAuthStore((state) => state.clearSession);
  const user = useAuthStore((state) => state.user);
  const setNotificationsOpen = useUiStore((state) => state.setNotificationsOpen);
  const { unreadCount } = useNotifications();

  return (
    <header className="flex items-center justify-between border-b border-border bg-panel px-6 py-4">
      <div>
        <p className="text-lg font-semibold text-slate-100">Welcome back</p>
        <p className="text-sm text-muted">Track portfolios, strategies, and backtests in one place.</p>
      </div>
      <div className="flex items-center gap-3">
        <button
          className="secondary-btn relative px-3"
          onClick={() => setNotificationsOpen(true)}
          type="button"
        >
          <Bell className="h-4 w-4" />
          {unreadCount > 0 ? (
            <span className="absolute -right-1 -top-1 flex h-5 min-w-5 items-center justify-center rounded-full bg-danger px-1 text-[10px] font-semibold text-white">
              {unreadCount}
            </span>
          ) : null}
        </button>
        <div className="hidden rounded-xl border border-border bg-slate-900/70 px-3 py-2 text-sm text-slate-200 md:block">
          {user?.email ?? "User"}
        </div>
        <button
          className="secondary-btn px-3"
          type="button"
          onClick={() => {
            clearSession();
            router.replace("/login");
          }}
        >
          <LogOut className="h-4 w-4" />
        </button>
      </div>
    </header>
  );
}
