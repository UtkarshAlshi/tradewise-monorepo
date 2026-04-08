"use client";

import { useEffect, useRef } from "react";
import type { Client, IMessage, StompSubscription } from "@stomp/stompjs";
import { createStompClient } from "@/lib/websocket/stomp-client";
import { useAuthStore } from "@/store/auth-store";
import { useNotificationsStore } from "@/store/notifications-store";

type Props = {
  trackedSymbols?: string[];
  onPriceMessage?: (symbol: string, payload: unknown) => void;
  children: React.ReactNode;
};

export function WebsocketProvider({ trackedSymbols = [], onPriceMessage, children }: Props) {
  const token = useAuthStore((state) => state.token);
  const pushNotification = useNotificationsStore((state) => state.pushNotification);
  const clientRef = useRef<Client | null>(null);
  const subscriptionsRef = useRef<StompSubscription[]>([]);

  useEffect(() => {
    if (!token) return;

    const client = createStompClient(token);
    clientRef.current = client;

    client.onConnect = () => {
      subscriptionsRef.current.push(
        client.subscribe("/user/queue/notifications", (message: IMessage) => {
          try {
            pushNotification(JSON.parse(message.body));
          } catch {
            pushNotification({ message: message.body, read: false, createdAt: new Date().toISOString() });
          }
        }),
      );

      trackedSymbols.forEach((symbol) => {
        subscriptionsRef.current.push(
          client.subscribe(`/topic/prices/${symbol.toUpperCase()}`, (message) => {
            const payload = JSON.parse(message.body);
            onPriceMessage?.(symbol.toUpperCase(), payload);
          }),
        );
      });
    };

    client.activate();

    return () => {
      subscriptionsRef.current.forEach((subscription) => subscription.unsubscribe());
      subscriptionsRef.current = [];
      client.deactivate();
      clientRef.current = null;
    };
  }, [token, trackedSymbols.join("|"), onPriceMessage, pushNotification]);

  return <>{children}</>;
}
