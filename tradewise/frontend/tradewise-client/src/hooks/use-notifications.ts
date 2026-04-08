"use client";

import { useNotificationsStore } from "@/store/notifications-store";

export function useNotifications() {
  const items = useNotificationsStore((state) => state.items);
  const markAllRead = useNotificationsStore((state) => state.markAllRead);
  const unreadCount = useNotificationsStore((state) => state.unreadCount());

  return { items, markAllRead, unreadCount };
}
