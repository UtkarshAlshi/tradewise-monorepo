"use client";

import { create } from "zustand";
import type { NotificationItem } from "@/types/notification";

type NotificationsState = {
  items: NotificationItem[];
  pushNotification: (item: NotificationItem) => void;
  markAllRead: () => void;
  unreadCount: () => number;
};

export const useNotificationsStore = create<NotificationsState>((set, get) => ({
  items: [],
  pushNotification: (item) => set((state) => ({ items: [item, ...state.items] })),
  markAllRead: () => set((state) => ({ items: state.items.map((item) => ({ ...item, read: true })) })),
  unreadCount: () => get().items.filter((item) => !item.read).length,
}));
