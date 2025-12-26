import { useState, useEffect } from 'react';
import { API_BASE_URL } from '@/lib/utils';

interface User {
  id: string;
  email: string;
}

export function useAuth() {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    const fetchUser = async () => {
      const token = localStorage.getItem('token');
      if (token) {
        try {
          const res = await fetch(`${API_BASE_URL}/api/users/me`, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          if (res.ok) {
            const userData = await res.json();
            setUser(userData);
            console.log('🔐 Logged in as:', userData.email);
          } else {
            setUser(null);
          }
        } catch (error) {
          console.error('Failed to fetch user:', error);
          setUser(null);
        }
      }
    };

    fetchUser();

    // Listen for token changes to re-fetch user
    window.addEventListener('tokenChanged', fetchUser);
    return () => {
      window.removeEventListener('tokenChanged', fetchUser);
    };
  }, []);

  return { user };
}