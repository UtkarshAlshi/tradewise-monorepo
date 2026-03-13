# TradeWise

TradeWise is a distributed algorithmic trading simulation platform that allows users to design trading strategies, backtest them against historical data, and observe real-time paper-trading performance without risking actual capital.

The platform is designed as a **microservices-based system** to separate responsibilities such as market data ingestion, strategy management, portfolio tracking, and real-time notifications. This architecture improves modularity, fault isolation, and system clarity.

TradeWise is intended as an engineering exploration into **distributed systems, event-driven architecture, and real-time data streaming in financial applications.**

---

# High-Level Architecture

TradeWise follows a **microservices architecture** with an API Gateway acting as the central entry point for client requests. Live market prices are ingested by the Market Data Service and distributed through Kafka for downstream services such as the Notification Service to react asynchronously.

Persistence is handled through a **shared PostgreSQL instance with logically isolated databases per service**.

```mermaid
flowchart TB
    subgraph Client_Layer["Client Layer"]
        U[User]
        FE[Frontend Dashboard<br/>Next.js 14 + TypeScript + Tailwind + Shadcn/UI]
    end

    subgraph Gateway_Layer["Gateway Layer"]
        GW[API Gateway<br/>Spring Cloud Gateway<br/>JWT Authentication + Routing]
    end

    subgraph Core_Services["Core Services"]
        US[User Service<br/>Profiles, Registration, Login]
        PS[Portfolio Service<br/>Holdings, Cash Balance, Transactions]
        SS[Strategy Service<br/>CRUD for Trading Rules]
        MD[Market Data Service<br/>Live Price Ingestion from Finnhub]
        BS[Backtesting Service<br/>Historical Simulation using ta4j]
        NS[Notification Service<br/>Real-time WebSocket Alerts]
        LS[Leaderboard Service<br/>Rankings and Aggregation]
    end

    subgraph Messaging_Layer["Messaging Layer"]
        K[(Apache Kafka)]
    end

    subgraph Data_Layer["Data Layer"]
        PG[(PostgreSQL<br/>Shared instance with logical per-service databases)]
    end

    subgraph External_Provider["External Provider"]
        FH[Finnhub API<br/>WebSocket Market Feed]
    end

    U --> FE
    FE --> GW

    GW --> US
    GW --> PS
    GW --> NS

    FH --> MD
    MD --> K
    K --> NS

    BS --> SS
    BS --> MD

    LS --> PS
    LS --> MD

    NS -->|WebSocket / STOMP| FE

    US --> PG
    PS --> PG
    SS --> PG
    NS --> PG


Service Breakdown
Service	Port	Description
API Gateway	8000	Entry point for frontend requests. Handles routing and JWT authentication.
User Service	8081	Manages user registration, authentication, and profiles.
Portfolio Service	8082	Tracks holdings, balances, and simulated trading activity.
Strategy Service	8083	CRUD operations for trading strategies defined by users.
Market Data Service	8084	Streams live market prices from external APIs and publishes events to Kafka.
Backtesting Service	8085	Executes strategy simulations against historical data using ta4j.
Notification Service	8086	Consumes Kafka events and pushes real-time updates to the UI via WebSockets.
Leaderboard Service	8087	Aggregates portfolio performance and ranks users.
Tech Stack
Backend Infrastructure

Java 17

Spring Boot 3

Spring Cloud Gateway

Apache Kafka

PostgreSQL

Docker & Docker Compose

ta4j (Technical analysis library for strategy simulation)

Frontend

Next.js 14

TypeScript

Tailwind CSS

Shadcn/UI

Recharts

Zustand

Key Features
Microservice-Oriented Architecture

Each domain concern (user management, strategies, portfolio tracking, notifications) is separated into its own service.

Event-Driven Data Flow

Market prices are broadcast via Kafka, allowing downstream services to react asynchronously.

Real-Time Market Updates

WebSocket-based notifications push live price updates to the UI without page refresh.

Strategy Simulation

Users can backtest trading strategies against historical data before applying them in a simulated environment.

Secure Gateway Layer

All frontend communication passes through the API Gateway which handles routing and authentication.

Modern Dashboard UI

The frontend provides a real-time trading dashboard experience with responsive charts and live updates.

Running the Project
Prerequisites

You will need:

Docker Desktop

Node.js 18+

npm

Start Backend Infrastructure
docker-compose up --build

This command launches:

PostgreSQL

Kafka

All backend microservices

The first startup may take 2–4 minutes while containers build.

Start Frontend
cd frontend/tradewise-client

npm install

npm run dev
Access the Application

Open:

http://localhost:3000
Development Notes

TradeWise was built primarily to explore:

distributed system architecture

real-time event streaming

service-oriented backend design

simulation environments for financial applications

The system intentionally separates heavy workloads (backtesting) from light request-response flows (auth, CRUD) to mirror patterns commonly used in scalable financial systems.

Future Improvements

Planned improvements include:

Distributed tracing using Zipkin or Jaeger

Machine learning service for market sentiment analysis

Kubernetes deployment for orchestration

Integration with broker APIs for real market execution

Advanced analytics dashboards

License

This project is intended for educational and experimental purposes.
