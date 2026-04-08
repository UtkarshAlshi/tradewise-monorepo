"use client";

import { Client, IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const WS_BASE_URL = process.env.NEXT_PUBLIC_WS_BASE_URL ?? "http://localhost:8000";

export function createStompClient(token: string) {
  return new Client({
    webSocketFactory: () => new SockJS(`${WS_BASE_URL}/ws?token=${token}`),
    reconnectDelay: 5000,
    debug: () => undefined,
  });
}

export type StompMessageHandler = (message: IMessage) => void;
