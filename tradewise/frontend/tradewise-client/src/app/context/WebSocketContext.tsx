'use client';

import React, { createContext, useContext, useEffect, useState, useRef } from 'react';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { WS_BASE_URL } from '@/lib/utils';

interface WebSocketContextType {
  subscribe: (topic: string, callback: (message: any) => void) => StompSubscription | null;
  isConnected: boolean;
}

const WebSocketContext = createContext<WebSocketContextType | null>(null);

export const WebSocketProvider = ({ children }: { children: React.ReactNode }) => {
  const [isConnected, setIsConnected] = useState(false);
  const stompClientRef = useRef<Client | null>(null);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => {
        try {
          const base = WS_BASE_URL.replace(/\/$/, '');
          
          if (typeof window !== 'undefined') {
            const token = localStorage.getItem('token');
            // Append /ws to the base URL (which is now localhost:8000)
            const url = token ? `${base}/ws?token=${encodeURIComponent(token)}` : `${base}/ws`;
            console.log('Connecting to WebSocket:', url);
            return new SockJS(url);
          }
          return new SockJS(`${base}/ws`);
        } catch (err) {
          console.error('WebSocket connection error:', err);
          return new SockJS(`${WS_BASE_URL.replace(/\/$/, '')}/ws`);
        }
      },
      onConnect: () => {
        console.log('✅ WebSocket connected!');
        setIsConnected(true);
      },
      onDisconnect: () => {
        console.log('⚠️ WebSocket disconnected!');
        setIsConnected(false);
      },
      onStompError: (frame) => {
        console.error('❌ STOMP error:', frame.headers['message']);
        console.error('Details:', frame.body);
      },
      // Add reconnection logic
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    stompClientRef.current = client;
    client.activate();

    return () => {
      client.deactivate();
    };
  }, []);

  const subscribe = (topic: string, callback: (message: any) => void): StompSubscription | null => {
    if (stompClientRef.current && stompClientRef.current.connected) {
      return stompClientRef.current.subscribe(topic, (message: IMessage) => {
        callback(JSON.parse(message.body));
      });
    }
    console.warn('Cannot subscribe - WebSocket not connected');
    return null;
  };

  return (
    <WebSocketContext.Provider value={{ subscribe, isConnected }}>
      {children}
    </WebSocketContext.Provider>
  );
};

export const useWebSocket = () => {
  const context = useContext(WebSocketContext);
  if (!context) {
    throw new Error('useWebSocket must be used within a WebSocketProvider');
  }
  return context;
};