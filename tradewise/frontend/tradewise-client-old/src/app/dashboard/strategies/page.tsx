'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import BacktestModal from '@/app/components/BacktestModal';
import { API_BASE_URL } from '@/lib/utils';

// --- Define Types ---
interface Strategy {
  id: string;
  name: string;
  description: string;
  createdAt: string;
  userId: string;
}

export default function StrategiesPage() {
  const router = useRouter();
  const [strategies, setStrategies] = useState<Strategy[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // --- Modal State ---
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedStrategy, setSelectedStrategy] = useState<Strategy | null>(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      router.push('/login');
      return;
    }

    const fetchData = async () => {
      try {
        const res = await fetch(`${API_BASE_URL}/api/strategies`, {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });

        if (!res.ok) {
          throw new Error('Failed to fetch strategies');
        }

        const data: Strategy[] = await res.json();
        setStrategies(data);
      } catch (err: any) {
        setError(err.message);
        if (err.status === 401 || err.status === 403) {
          localStorage.removeItem('token');
          router.push('/login');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [router]);

  // --- Modal Handlers ---
  const openBacktestModal = (strategy: Strategy) => {
    setSelectedStrategy(strategy);
    setIsModalOpen(true);
  };

  const closeBacktestModal = () => {
    setIsModalOpen(false);
    setSelectedStrategy(null);
  };

  return (
    <div className="min-h-screen p-8">
      <header className="flex justify-between items-center mb-12">
        <h1 className="text-4xl font-bold">My Strategies</h1>
        <Link
          href="/strategies/new"
          className="py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-md transition duration-200"
        >
          Create New Strategy
        </Link>
      </header>

      <main>
        {loading && <p>Loading strategies...</p>}
        {error && <p className="text-red-500">{error}</p>}
        
        {!loading && !error && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {strategies.length > 0 ? (
              strategies.map((strategy) => (
                <div key={strategy.id} className="bg-gray-800 p-6 rounded-lg shadow-md flex flex-col justify-between">
                  <div>
                    <h3 className="text-xl font-bold mb-2">{strategy.name}</h3>
                    <p className="text-gray-400 mb-4">{strategy.description}</p>
                  </div>
                  <button
                    onClick={() => openBacktestModal(strategy)}
                    className="w-full mt-4 py-2 px-4 bg-green-600 hover:bg-green-700 text-white font-semibold rounded-md"
                  >
                    Run Backtest
                  </button>
                </div>
              ))
            ) : (
              <p>You have not created any strategies yet.</p>
            )}
          </div>
        )}
      </main>

      {isModalOpen && selectedStrategy && (
        <BacktestModal
          strategyId={selectedStrategy.id}
          strategyName={selectedStrategy.name}
          onClose={closeBacktestModal}
        />
      )}
    </div>
  );
}