// src/hooks/useAuthToken.ts
import { useState, useEffect } from 'react';

export function useAuthToken() {
  const [token, setToken] = useState<string | null>(null);

  useEffect(() => {
    // Function to update token from localStorage
    const updateToken = () => {
      const storedToken = typeof window !== 'undefined' ? localStorage.getItem('token') : null;
      setToken(storedToken);
    };

    // Initial check
    updateToken();

    // Listen for custom 'tokenChanged' event that we'll dispatch on login/logout
    window.addEventListener('tokenChanged', updateToken);

    // Cleanup listener on component unmount
    return () => {
      window.removeEventListener('tokenChanged', updateToken);
    };
  }, []);

  return token;
}

/**
 * Dispatches a custom event to notify all parts of the app that the auth token has changed.
 * This should be called after logging in (token set) or logging out (token removed).
 */
export function dispatchTokenChangedEvent() {
  if (typeof window !== 'undefined') {
    window.dispatchEvent(new Event('tokenChanged'));
  }
}
