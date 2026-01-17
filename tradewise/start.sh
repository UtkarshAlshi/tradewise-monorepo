#!/bin/bash

echo "🚀 Starting TradeWise Application..."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker Desktop first."
    exit 1
fi

# Navigate to the tradewise directory
cd "$(dirname "$0")"

echo "📦 Starting backend services..."
docker-compose up --build -d

echo ""
echo "⏳ Waiting for services to be ready..."
sleep 10

echo ""
echo "✅ Backend services are starting!"
echo ""
echo "📊 Service Status:"
docker-compose ps

echo ""
echo "🌐 Frontend: Run 'npm run dev' in the frontend/tradewise-client directory"
echo "🔗 API Gateway: http://localhost:8000"
echo "🔗 Frontend: http://localhost:3000"
echo ""
echo "📝 To view logs: docker-compose logs -f"
echo "🛑 To stop: docker-compose down"