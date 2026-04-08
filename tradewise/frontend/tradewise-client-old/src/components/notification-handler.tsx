"use client"

import { useEffect } from 'react';
import { useWebSocket } from '@/app/context/WebSocketContext';
import { toast } from 'react-hot-toast';
import { useAuth } from '@/hooks/useAuth'; 

const NotificationHandler = () => {
  const { subscribe, isConnected } = useWebSocket();
  const { user } = useAuth();

  useEffect(() => {
    if (isConnected && user?.email) {
      console.log(`✅ Subscribing to notifications for ${user.email}`);

      const subscription = subscribe(`/user/${user.email}/queue/notifications`, (message) => {
        console.log('📢 Notification received:', message);
        
        if (message && message.text) {
          toast.info(message.text);
        }
      });

      return () => {
        subscription?.unsubscribe();
      };
    }
  }, [isConnected, user, subscribe]);

  return null; 
};

export default NotificationHandler;