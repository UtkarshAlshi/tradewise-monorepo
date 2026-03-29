#!/bin/bash

set -e

echo "🚀 Starting TradeWise Application..."
echo ""

if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

cd "$(dirname "$0")"

echo "📦 Starting backend services..."
docker compose up --build -d

echo ""
echo "⏳ Waiting briefly for services to initialize..."
sleep 10

echo ""
echo "📊 Service Status:"
docker compose ps

echo ""
echo "✅ Backend services are starting!"
echo "🔗 API Gateway: http://localhost:8000"
echo "🔗 Frontend: http://localhost:3000"
echo ""
echo "🌐 Frontend: Run 'npm run dev' in frontend/tradewise-client"
echo "📝 To view logs: docker compose logs -f"
echo "🛑 To stop: docker compose down"