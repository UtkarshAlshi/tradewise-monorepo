import type { NextConfig } from "next";

const config: NextConfig = {
  // This line explicitly tells Next.js where the project root is,
  // which fixes the "inferred workspace root" warning.
  outputFileTracingRoot: __dirname,
  async rewrites() {
    // Forward websocket info requests to the backend API Gateway (port 8000)
    // This ensures all client-side API and WebSocket traffic goes through the central gateway.
    const dest = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8000'; // Correctly point to API Gateway

    return [
      {
        source: '/ws/:path*',
        destination: `${dest.replace(/\/$/, '')}/ws/:path*`,
      },
      {
        source: '/ws',
        destination: `${dest.replace(/\/$/, '')}/ws`,
      },
    ];
  },
};

export default config;
