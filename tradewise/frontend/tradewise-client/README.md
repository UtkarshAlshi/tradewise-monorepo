# TradeWise Frontend

Fresh frontend for the TradeWise microservices backend.

## Stack
- Next.js
- TypeScript
- Tailwind CSS
- TanStack Query
- Zustand
- React Hook Form + Zod
- SockJS + STOMP

## Setup
1. Copy `.env.example` to `.env.local`
2. Install dependencies
3. Start the dev server

```bash
npm install
npm run dev
```

## Required backend
The frontend expects the gateway at `http://localhost:8000`.
