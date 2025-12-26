# TradeWise 📈

**TradeWise** is a high-performance, distributed algorithmic trading platform. It allows users to design trading strategies, backtest them against historical data, and execute paper trades in real-time using a microservices architecture.

---

## 🏗️ Architecture

TradeWise is built as a **Microservices Monorepo**. It moves away from monolithic constraints, allowing independent scaling of the backtesting engine and real-time market data processors.

### Service Breakdown

| Service | Port | Description |
| :--- | :--- | :--- |
| **API Gateway** | `8000` | Central entry point. Handles routing, JWT Authentication, and rate limiting. |
| **User Service** | `8081` | Manages user registration, login, and secure profiles. |
| **Portfolio Service** | `8082` | Tracks cash balance, holdings, and transaction history. |
| **Strategy Service** | `8083` | CRUD for trading rules (e.g., "Buy if RSI < 30"). |
| **Market Data Service** | `8084` | Fetches live prices from Finnhub API and publishes to Kafka. |
| **Backtesting Service** | `8085` | Runs historical simulations using `ta4j` to validate strategies. |
| **Notification Service** | `8086` | Consumes Kafka events to push Real-time WebSocket alerts to the frontend. |

---

## 🛠️ Tech Stack

### Backend Infrastructure
*   **Java 17 & Spring Boot 3:** Core application framework.
*   **Apache Kafka:** Event-streaming backbone for real-time price updates.
*   **PostgreSQL:** Relational database (Per-service database isolation).
*   **Docker & Docker Compose:** Containerization and orchestration.
*   **Ta4j:** Technical Analysis library for strategy logic.

### Frontend Client
*   **Next.js 14 (App Router):** React framework for production.
*   **TypeScript:** Type safety.
*   **Tailwind CSS & Shadcn/UI:** Professional, accessible UI components.
*   **Recharts:** Financial data visualization.
*   **Zustand:** State management.

---

## 🚀 Getting Started

You can run the entire infrastructure (Databases, Kafka, Zookeeper, and 7 Microservices) with one command.

### Prerequisites
*   Docker Desktop installed and running.
*   Node.js 18+ (for frontend only).

### Installation

1.  **Clone the repository**
    ```bash
    git clone https://github.com/your-username/tradewise.git
    cd tradewise
    ```

2.  **Start the Backend (Docker)**
    This provisions Postgres, initializes databases, starts Kafka, builds the JARs, and launches the services.
    ```bash
    cd tradewise
    docker-compose up --build
    ```
    *Wait for the logs to stabilize (approx. 2-4 minutes on first run).*

3.  **Start the Frontend**
    Open a new terminal:
    ```bash
    cd tradewise/frontend/tradewise-client
    npm install
    npm run dev
    ```

4.  **Access the App**
    Open [http://localhost:3000](http://localhost:3000) in your browser.

---

## ⚡ Key Features

*   **Microservice Isolation:** Failure in the Backtesting service does not crash the Trading engine.
*   **Event-Driven Architecture:** Market data is broadcasted via Kafka; services subscribe asynchronously.
*   **JWT Security:** Stateless authentication passed through the API Gateway.
*   **Real-time WebSockets:** Prices update live on the dashboard without page refreshes.
*   **Dark Mode UI:** Professional financial dashboard aesthetic.

---

## 🔮 Future Roadmap

*   [ ] Integration with Interactive Brokers API for real-money execution.
*   [ ] Machine Learning (Python/FastAPI) service for sentiment analysis.
*   [ ] Kubernetes (K8s) deployment configuration.

---
