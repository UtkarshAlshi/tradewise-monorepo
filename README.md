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
