# 🏆 TradeWise: Real-Time Investment & Portfolio Analytics Platform

A scalable, modern web application for analyzing, simulating, and tracking live trading strategies with a professional SaaS dashboard interface.

```
   ████████╗██████╗  █████╗ ██████╗ ███████╗██╗    ██╗██╗███████╗███████╗
   ╚══██╔══╝██╔══██╗██╔══██╗██╔══██╗██╔════╝██║    ██║██║██╔════╝██╔════╝
      ██║   ██████╔╝███████║██║  ██║█████╗  ██║ █╗ ██║██║███████╗█████╗  
      ██║   ██╔══██╗██╔══██║██║  ██║██╔══╝  ██║███╗██║██║╚════██║██╔══╝  
      ██║   ██║  ██║██║  ██║██████╔╝███████╗╚███╔███╔╝██║███████║███████╗
      ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝╚═════╝ ╚══════╝ ╚══▀▀▀══╝ ╚═╝╚══════╝╚══════╝
```

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Development Phases](#development-phases)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)

---

## 🎯 Overview

TradeWise is a comprehensive investment platform that combines:

- **Real-time portfolio tracking** with live market data
- **Strategy builder** for creating and testing trading rules
- **Backtesting engine** to validate strategies on historical data
- **Analytics dashboard** with professional visualizations
- **Global leaderboard** ranking strategies by Sharpe ratio
- **Microservice architecture** for scalability and independence
- **Cloud-ready deployment** (Docker, Kubernetes, Oracle Cloud)

Perfect for:
- Individual traders exploring trading strategies
- Finance professionals building algorithmic systems
- Portfolio managers tracking multiple strategies
- Educational purposes (learning quantitative finance)

---

## ✨ Core Features

### 🔐 **User Authentication & Authorization**
- JWT-based authentication
- OAuth 2.0 (Google/GitHub sign-in) - *Coming soon*
- Role-based access control (RBAC)
- Session management

### 📊 **Portfolio Management**
- Create and manage multiple portfolios
- Track stocks, ETFs, and cryptocurrencies
- Real-time portfolio value calculations
- Asset allocation visualization
- Diversification analysis

### 📈 **Live Market Feed**
- Real-time price updates via WebSockets
- Integration with Finnhub API
- Market data caching with Redis
- Price tick history

### 🔨 **Strategy Builder**
- Visual strategy creation interface
- Pre-built strategy templates
- Custom rule builder (RSI, MACD, Moving Averages, etc.)
- Real-time strategy parameters
- Strategy versioning

### 🎲 **Backtesting Engine**
- Run strategies against historical data
- Performance metrics (Sharpe ratio, max drawdown, etc.)
- Trade-by-trade analysis
- Optimization and sensitivity analysis
- Results visualization

### 📊 **Analytics Dashboard**
- Portfolio performance charts
- Returns analysis (daily, monthly, yearly)
- Risk metrics (volatility, drawdown, Value at Risk)
- Heatmaps and correlation matrices
- Custom report generation

### 🏅 **Global Leaderboard**
- Rank strategies by Sharpe ratio
- Filter by asset class or time period
- Community insights
- Strategy popularity metrics

### 🔔 **Notification System**
- Price alerts (triggers & thresholds)
- Strategy execution notifications
- Portfolio milestone alerts
- Real-time WebSocket delivery
- Email/SMS integration - *Coming soon*

---

## 🔧 Tech Stack

### **Frontend**
| Component | Technology |
|-----------|-----------|
| Framework | Next.js 16 (React 19) |
| Styling | Tailwind CSS v4 |
| Components | Shadcn/UI + Radix UI |
| Icons | Lucide React |
| Charts | Recharts (coming soon) |
| State Management | React Hooks |
| Forms | React Hook Form |
| Language | TypeScript |

### **Backend**
| Component | Technology |
|-----------|-----------|
| Framework | Spring Boot 3 |
| Language | Java 17+ |
| API Gateway | Spring Cloud Gateway |
| Authentication | JWT + Spring Security |
| Database | PostgreSQL (primary) |
| Cache | Redis |
| Message Queue | Kafka |
| API Documentation | Swagger/OpenAPI |

### **Microservices**
```
api-gateway/          - Request routing & authentication
├── user-service/    - User management & authentication
├── portfolio-service/ - Portfolio & asset management
├── strategy-service/ - Strategy CRUD & templates
├── market-data-service/ - Real-time market data
├── backtesting-service/ - Backtest execution engine
└── notification-service/ - Alerts & notifications
```

### **Infrastructure**
| Component | Technology |
|-----------|-----------|
| Containerization | Docker |
| Orchestration | Kubernetes (planned) |
| Hosting | Oracle Cloud (free tier) |
| Frontend CDN | Vercel |
| Data Source | Finnhub API, yFinance |

---

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ (frontend)
- Java 17+ (backend)
- Docker & Docker Compose
- PostgreSQL 14+
- Redis

### Installation

#### 1. Clone Repository
```bash
git clone https://github.com/yourusername/tradewise.git
cd tradewise
```

#### 2. Frontend Setup
```bash
cd tradewise/frontend/tradewise-client

# Install dependencies
npm install

# Run development server
npm run dev

# Build for production
npm run build
```

Access at: **http://localhost:3000**

#### 3. Backend Setup
```bash
# Start all services with Docker Compose
cd tradewise
docker-compose up -d

# Or run individual services:
cd tradewise/backend/api-gateway
mvn spring-boot:run

cd tradewise/backend/user-service
mvn spring-boot:run
```

API endpoints available at:
- **API Gateway:** http://localhost:8000
- **User Service:** http://localhost:8081
- **Portfolio Service:** http://localhost:8082

#### 4. Login to Dashboard
```
Email: demo@tradewise.com
Password: DemoPassword123!
URL: http://localhost:3000/login
```

---

## 📁 Project Structure

```
tradewise/
│
├── frontend/
│   └── tradewise-client/                 # Next.js + React app
│       ├── src/
│       │   ├── app/
│       │   │   ├── dashboard/            # Dashboard layout & pages
│       │   │   ├── login/
│       │   │   ├── register/
│       │   │   └── ...
│       │   ├── components/
│       │   │   ├── dashboard-nav.tsx
│       │   │   └── ui/                   # Shadcn/UI components
│       │   └── lib/
│       ├── public/
│       ├── package.json
│       └── tsconfig.json
│
├── backend/
│   ├── api-gateway/                      # Spring Cloud Gateway
│   │   └── src/main/java/
│   ├── user-service/                     # User & auth service
│   ├── portfolio-service/                # Portfolio management
│   ├── strategy-service/                 # Strategy management
│   ├── market-data-service/              # Market data provider
│   ├── backtesting-service/              # Backtest engine
│   └── notification-service/             # Notifications
│
├── docker/
│   ├── docker-compose.yml                # Multi-container orchestration
│   └── Dockerfile                        # Container specs
│
├── docs/
│   ├── API.md                            # API documentation
│   ├── ARCHITECTURE.md                   # System architecture
│   └── DEPLOYMENT.md                     # Deployment guide
│
└── README.md                             # This file
```

---

## 📈 Development Phases

### ✅ Phase 1: Monorepo Setup & Foundation
- [x] Create monorepo structure
- [x] Set up backend services
- [x] Create frontend scaffold
- [x] Database schema design
- [x] API documentation

### ✅ Phase 2: UI/UX Overhaul & Dashboard
- [x] Install Shadcn/UI component library
- [x] Implement dark mode with next-themes
- [x] Create professional auth pages
- [x] Build dashboard layout system
  - [x] Responsive sidebar navigation
  - [x] Header with user menu
  - [x] KPI cards grid
  - [x] Portfolio list integration
  - [x] Recent activity panel

### 🔄 Phase 2c: Visualizations & Charts
- [ ] Install Recharts library
- [ ] Portfolio performance chart (area chart)
- [ ] Strategy performance chart (line chart)
- [ ] Asset allocation chart (pie/donut)
- [ ] Market price chart (candlestick)
- [ ] Backtest results visualization

### 📋 Phase 3: Feature Pages
- [ ] Portfolio management page
- [ ] Strategy builder interface
- [ ] Backtesting page
- [ ] Settings & user preferences
- [ ] Leaderboard improvements

### 🔔 Phase 4: Real-time Features
- [ ] WebSocket integration for live data
- [ ] Real-time notification system
- [ ] Live strategy execution UI
- [ ] Market data streaming
- [ ] User notifications panel

### 🐳 Phase 5: Dockerization
- [ ] Create Dockerfile for each service
- [ ] Write docker-compose.yml
- [ ] Set up development environment with Docker
- [ ] Container health checks
- [ ] Multi-stage builds for optimization

### 🚀 Phase 6: Deployment
- [ ] Frontend: Deploy to Vercel
- [ ] Backend: Set up Oracle Cloud Free Tier
- [ ] Database: Configure PostgreSQL on cloud
- [ ] CI/CD: GitHub Actions pipeline
- [ ] Monitoring & logging setup

### 📚 Phase 7: Documentation & Polish
- [ ] API documentation (Swagger)
- [ ] Architecture diagrams
- [ ] Deployment guides
- [ ] Contributing guidelines
- [ ] Demo video recording

---

## 📡 API Documentation

### Base URLs
```
Development:  http://localhost:8000
Production:   https://api.tradewise.com
```

### Authentication
All endpoints (except `/auth/*`) require JWT token in header:
```
Authorization: Bearer {token}
```

### Key Endpoints

#### User Service
```
POST   /api/auth/login              # Login
POST   /api/auth/register           # Register
GET    /api/users/me                # Current user
PUT    /api/users/{id}              # Update profile
```

#### Portfolio Service
```
GET    /api/portfolios              # List user portfolios
POST   /api/portfolios              # Create portfolio
GET    /api/portfolios/{id}         # Get portfolio details
PUT    /api/portfolios/{id}         # Update portfolio
DELETE /api/portfolios/{id}         # Delete portfolio

GET    /api/portfolios/{id}/assets  # List portfolio assets
POST   /api/portfolios/{id}/assets  # Add asset to portfolio
```

#### Strategy Service
```
GET    /api/strategies              # List user strategies
POST   /api/strategies              # Create strategy
GET    /api/strategies/{id}         # Get strategy
PUT    /api/strategies/{id}         # Update strategy
DELETE /api/strategies/{id}         # Delete strategy
```

#### Backtesting Service
```
POST   /api/backtest                # Run backtest
GET    /api/backtest/{id}           # Get backtest results
GET    /api/backtest/{id}/trades    # Get individual trades
```

### Full API Documentation
See [docs/API.md](./docs/API.md) for complete endpoint reference with request/response examples.

---

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
   ```bash
   git clone https://github.com/yourusername/tradewise.git
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes**
   - Write clean, documented code
   - Follow the existing code style
   - Add tests for new features
   - Update documentation

4. **Commit your changes**
   ```bash
   git commit -m "Add: description of your feature"
   ```

5. **Push to GitHub**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create a Pull Request**
   - Describe your changes
   - Link related issues
   - Request review from maintainers

### Code Style
- **Frontend:** Prettier + ESLint (Next.js standards)
- **Backend:** Google Java Style Guide
- **Commit messages:** Semantic Commits
- **Documentation:** Markdown with code examples

---

## 📊 Current Status

| Component | Status | Notes |
|-----------|--------|-------|
| Frontend Foundation | ✅ Complete | Next.js, TailwindCSS, Shadcn/UI |
| Dashboard Layout | ✅ Complete | Responsive, production-ready |
| Authentication UI | ✅ Complete | Login, register pages |
| Backend Services | 🔄 In Progress | API Gateway, User Service |
| Database Schema | ✅ Complete | PostgreSQL with migrations |
| API Endpoints | 🔄 In Progress | RESTful API implementation |
| Charts & Visualization | ⏳ Planned | Recharts integration |
| Real-time Features | ⏳ Planned | WebSocket implementation |
| Deployment | ⏳ Planned | Docker, Vercel, Oracle Cloud |

---

## 📞 Support

### Getting Help
- **Documentation:** Check [docs/](./docs/) folder
- **Issues:** Create a GitHub issue with details
- **Discussions:** Start a discussion for questions
- **Email:** support@tradewise.com

### Reporting Bugs
Please include:
- Steps to reproduce
- Expected behavior
- Actual behavior
- Screenshots/error logs
- Browser and OS information

---

## 📄 License

This project is licensed under the MIT License - see [LICENSE](./LICENSE) file for details.

---

## 🎓 Learning Resources

This project is designed to teach:

- **Full-stack web development** with modern frameworks
- **Microservices architecture** with Spring Boot
- **Real-time systems** with WebSockets and message queues
- **Financial domain knowledge** and quantitative analysis
- **DevOps practices** with Docker and Kubernetes
- **Production readiness** and best practices

Ideal for:
- Computer Science students
- Junior developers learning system design
- Finance professionals getting into tech
- Anyone interested in trading technology

---

## 🙏 Acknowledgments

- [Shadcn/UI](https://ui.shadcn.com/) for excellent components
- [Tailwind CSS](https://tailwindcss.com/) for utility-first styling
- [Finnhub API](https://finnhub.io/) for market data
- [Spring Framework](https://spring.io/) for robust backend
- The open-source community for amazing tools

---

## 🚀 What's Next?

### Immediate Priorities
1. Complete Phase 2c - Add charts and visualizations
2. Build portfolio management features
3. Create strategy builder interface
4. Implement backtesting UI

### Long-term Vision
- Mobile app (React Native)
- Advanced analytics and ML models
- Automated trading execution
- Community features and social trading
- Institutional-grade reporting

---

## 📸 Screenshots

### Dashboard Overview
![Dashboard](./docs/screenshots/dashboard.png)

### Login Page
![Login](./docs/screenshots/login.png)

### Portfolio Management
![Portfolio](./docs/screenshots/portfolio.png) *(Coming soon)*

### Strategy Builder
![Strategy](./docs/screenshots/strategy.png) *(Coming soon)*

---

## 💬 Questions?

Feel free to:
- 📧 Email: developer@tradewise.com
- 💬 Discord: [Join our server](https://discord.gg/tradewise)
- 🐦 Twitter: [@TradeWiseApp](https://twitter.com/tradewiseapp)
- 📖 Docs: [docs.tradewise.com](https://docs.tradewise.com)

---

<div align="center">

## ⭐ If you find this project helpful, please give it a star!

**Made with ❤️ by the TradeWise Team**

[GitHub](https://github.com/tradewise/tradewise) • [Website](https://tradewise.com) • [Documentation](https://docs.tradewise.com)

</div>

---

**Last Updated:** December 19, 2025 | **Version:** 1.0.0 | **Status:** 🟢 Active Development
