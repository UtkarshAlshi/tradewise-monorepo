"use client";

import { create } from "zustand";

type UiState = {
  notificationsOpen: boolean;
  setNotificationsOpen: (value: boolean) => void;
};

export const useUiStore = create<UiState>((set) => ({
  notificationsOpen: false,
  setNotificationsOpen: (notificationsOpen) => set({ notificationsOpen }),
}));
