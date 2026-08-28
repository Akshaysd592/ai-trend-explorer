# 🚀 AI Trend Explorer

[![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot 3.3.2](https://img.shields.io/badge/Spring%20Boot-3.3.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Next.js 15](https://img.shields.io/badge/Next.js-15.0.3-black?style=for-the-badge&logo=next.js&logoColor=white)](https://nextjs.org/)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-3.7-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)](https://kafka.apache.org/)
[![Redis 7](https://img.shields.io/badge/Redis-7.x-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)
[![PostgreSQL 16](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Google Gemini](https://img.shields.io/badge/Gemini%20AI-3.5%20Flash%20Lite-8E75C2?style=for-the-badge&logo=google&logoColor=white)](https://ai.google.dev/)
[![Docker Compose](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)
[![Author](https://img.shields.io/badge/Author-Akshay-blue?style=for-the-badge&logo=github)](https://github.com/Akshaysd592)

> **An enterprise-grade, event-driven microservices platform designed to discover, aggregate, score, and AI-enrich trending open-source AI repositories and models in real time.**

---

## 📑 Table of Contents

- [Overview](#-overview)
- [System Architecture](#-system-architecture)
- [Event-Driven Ingestion & AI Pipeline](#-event-driven-ingestion--ai-pipeline)
- [Hexagonal Architecture (Ports & Adapters)](#-hexagonal-architecture-ports--adapters)
- [Microservices Catalog & Port Matrix](#-microservices-catalog--port-matrix)
- [REST API Reference](#-rest-api-reference)
- [Key Features](#-key-features)
- [Quickstart Guide](#-quickstart-guide)
- [Environment Configuration Matrix](#-environment-configuration-matrix)
- [Testing & Quality Assurance](#-testing--quality-assurance)
- [CI/CD Pipeline](#-cicd-pipeline)
- [Author & Maintainer](#-author--maintainer)
- [License](#-license)

---

## 🌟 Overview

**AI Trend Explorer** solves the challenge of discovering high-impact AI models and developer tools across fragmented ecosystems. It continuously ingests data from **GitHub** and **Hugging Face**, scores their growth velocity, and leverages **Google Gemini 3.5 Flash Lite** over an asynchronous **Apache Kafka** event pipeline to generate architectural summaries and category classifications.

### Key Capabilities:
- **Event-Driven Reactive Pipeline**: Fully decoupled ingestion and AI enrichment using Apache Kafka.
- **Hexagonal Architecture**: Strict domain boundary separation with incoming and outgoing ports.
- **Smart AI Summaries**: Automated categorizations and architectural insights via Google Gemini AI (`gemini-3.5-flash-lite`).
- **Resilient Multi-Layer Caching**: Redis 7 caching with automated fallback to PostgreSQL on cache anomalies.
- **Modern Full-Stack UI**: Next.js 15 App Router, React 19, TypeScript, Tailwind CSS, TanStack Query, and Redux Toolkit.
- **Enterprise Security**: Stateless JWT authentication (HMAC-SHA512), role-based access control, and Spring Cloud Gateway routing.
- **1-Command Containerization**: Production multi-stage Dockerfiles orchestrated via Docker Compose.

---

## 🏗️ System Architecture

```mermaid
flowchart TB
    subgraph ClientTier["Client Tier"]
        FE["Next.js 15 Dashboard (:3000)<br/>React 19 / TypeScript / Tailwind / TanStack Query"]
    end

    subgraph GatewayTier["Gateway & Security Tier"]
        GW["API Gateway (:8080)<br/>Spring Cloud Gateway / CORS / Dynamic Routing"]
    end

    subgraph ServiceTier["Core Microservices Tier"]
        AUTH["Auth Service (:8082)<br/>Spring Security 6 / JWT (HMAC-SHA512)"]
        TREND["Trend Service (:8081)<br/>Hexagonal Architecture / Ingestion Engine / Velocity Scorer"]
        ANALYSIS["AI Analysis Service (:8083)<br/>Kafka Consumer / Gemini 3.5 Flash Lite Adapter"]
    end

    subgraph EventStreaming["Event Streaming Tier (Kafka)"]
        KAFKA["Apache Kafka (:9092)<br/>Topics: ai.trends.ingested (P=3), ai.trends.enriched (P=3)"]
    end

    subgraph DataTier["Data & Cache Persistence Tier"]
        AUTH_DB[("auth_db (:5434)<br/>PostgreSQL 16 / Liquibase")]
        TREND_DB[("trend_db (:5433)<br/>PostgreSQL 16 / Liquibase")]
        REDIS[("Redis 7 (:6379)<br/>TTL: 5m / CacheErrorHandler")]
    end

    FE -->|REST Requests| GW
    GW -->|/api/v1/auth/**| AUTH
    GW -->|/api/v1/trends/**| TREND
    GW -->|/api/v1/analysis/**| ANALYSIS

    AUTH --> AUTH_DB
    TREND --> TREND_DB
    TREND <--> REDIS

    TREND -->|Publish: ai.trends.ingested| KAFKA
    KAFKA -->|Consume: ai.trends.ingested| ANALYSIS
    ANALYSIS -->|Google Gemini API| GEMINI[("Google Gemini 3.5 Flash Lite")]
    ANALYSIS -->|Publish: ai.trends.enriched| KAFKA
    KAFKA -->|Consume: ai.trends.enriched| TREND
```

---

## 🔄 Event-Driven Ingestion & AI Pipeline

```mermaid
sequenceDiagram
    autonumber
    actor User as User / Scheduler
    participant GW as API Gateway (:8080)
    participant TS as Trend Service (:8081)
    participant DB as trend_db (PostgreSQL)
    participant Redis as Redis Cache (:6379)
    participant Kafka as Apache Kafka (:9092)
    participant AI as AI Analysis Service (:8083)
    participant Gemini as Gemini 3.5 Flash Lite

    User->>GW: POST /api/v1/trends/ingest (Bearer Token)
    GW->>TS: Forward Ingestion Request
    TS->>TS: Ingest from GitHub & Hugging Face
    TS->>TS: Compute Velocity Score
    TS->>DB: Save Ingested Trends
    TS->>Redis: Evict "trends" Cache
    TS->>Kafka: Publish TrendIngestedEvent to 'ai.trends.ingested'
    TS-->>User: 200 OK (Ingestion Summary)

    Kafka->>AI: Consume TrendIngestedEvent
    AI->>Gemini: Request Classification & Architectural Summary
    Gemini-->>AI: Category + Technical Summary
    AI->>Kafka: Publish TrendEnrichedEvent to 'ai.trends.enriched'

    Kafka->>TS: Consume TrendEnrichedEvent
    TS->>DB: Update Trend (ai_category, ai_summary)
    TS->>Redis: Evict "trends" Cache
```

---

## 🔷 Hexagonal Architecture (Ports & Adapters)

Each backend service strictly adheres to **Hexagonal Architecture (Ports & Adapters)** principles to ensure zero business logic coupling with external frameworks:

```text
com.aitrend.trend/
├── domain/                     # Pure Business Logic & Domain Models (No framework annotations)
│   ├── model/                  # Trend, SourceType, IngestionResult
│   └── exception/              # TrendNotFoundException, DomainValidationException
├── application/                # Application Use Cases & Ports
│   ├── port/in/                # Inbound Ports (TrendUseCase, IngestTrendsUseCase, Commands/Queries)
│   ├── port/out/               # Outbound Ports (TrendRepositoryPort, IngestionSourcePort, EventPublisherPort)
│   └── service/                # Application Services orchestrating Use Cases
├── adapter/                    # Ports Implementations (Adapters)
│   ├── in/
│   │   ├── web/                # REST Controllers (OpenAPI Generated Specs)
│   │   └── kafka/              # Kafka Consumers (TrendEnrichedEventListener)
│   └── out/
│       ├── persistence/        # Spring Data JPA, Hibernate, PostgreSQL Entities
│       ├── ingestion/          # GitHub / Hugging Face WebClient Ingestion Adapters
│       ├── ai/                 # Gemini AI Adapter & RestClient
│       └── kafka/              # Kafka Producers (EventPublisherAdapter)
└── infrastructure/             # Cross-Cutting Infrastructure
    ├── config/                 # Redis, Kafka, Security, OpenAPI Bean Configurations
    └── exception/              # GlobalExceptionHandler, ProblemDetail RFC 7807
```

---

## 📊 Microservices Catalog & Port Matrix

| Service / Container | Internal Port | Host Port | Protocol | Health Endpoint | Description |
|---|---|---|---|---|---|
| **`frontend`** | `3000` | `3000` | HTTP | `GET /` | Next.js 15 Standalone Web Application |
| **`api-gateway`** | `8080` | `8080` | HTTP | `/actuator/health` | Spring Cloud Gateway (CORS, Routing, Rate Limiting) |
| **`trend-service`** | `8081` | `8081` | HTTP | `/actuator/health` | Hexagonal Trend Ingestion, Scoring, and Repository |
| **`auth-service`** | `8082` | `8082` | HTTP | `/actuator/health` | Spring Security 6, JWT Authentication & User Management |
| **`ai-analysis-service`** | `8083` | `8083` | HTTP | `/actuator/health` | Gemini 3.5 Flash Lite Kafka Consumer & AI Enrichment |
| **`kafka`** | `9092` | `9092` | TCP | Native Broker | Apache Kafka 3.7 Event Streaming Broker |
| **`redis`** | `6379` | `6379` | RESP | `redis-cli ping` | Redis 7 Distributed Cache with CacheErrorHandler |
| **`trend-postgres`** | `5432` | `5433` | PostgreSQL | `pg_isready` | PostgreSQL 16 Database for `trend_db` |
| **`auth-postgres`** | `5432` | `5434` | PostgreSQL | `pg_isready` | PostgreSQL 16 Database for `auth_db` |

---

## 📡 REST API Reference

### 1. Authentication Service (`/api/v1/auth`)

| Method | Endpoint | Auth | Request Body | Description |
|---|---|---|---|---|
| `POST` | `/api/v1/auth/register` | None | `{ "email", "password", "firstName", "lastName" }` | Register a new user account |
| `POST` | `/api/v1/auth/login` | None | `{ "email", "password" }` | Authenticate and obtain JWT access & refresh tokens |
| `GET` | `/api/v1/auth/me` | Bearer | — | Get authenticated user profile |

### 2. Trend Explorer Service (`/api/v1/trends`)

| Method | Endpoint | Auth | Query Parameters | Description |
|---|---|---|---|---|
| `GET` | `/api/v1/trends` | None | `source`, `language`, `q`, `page`, `size`, `sortBy`, `sortDir` | List paginated trends (Cached in Redis) |
| `GET` | `/api/v1/trends/{id}` | None | — | Retrieve single trend by ID |
| `POST` | `/api/v1/trends/ingest` | Bearer | — | Trigger ingestion from GitHub & Hugging Face |
| `PATCH`| `/api/v1/trends/{id}/ai-metadata` | Bearer | `{ "aiCategory", "aiSummary" }` | Update AI metadata for a trend |

### 3. AI Analysis Service (`/api/v1/analysis`)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/analysis/trend/{id}` | None | Trigger on-demand Gemini AI analysis for a specific trend |

---

## 🎯 Key Features

### 1. Next.js 15 Standalone Dashboard
- **Reactive UI**: Filter trends by source (`GITHUB`, `HUGGING_FACE`), programming language, keyword, and sorting (stars, velocity score, date).
- **Dedicated Details View (`/trends/[id]`)**: Full deep-dive page with velocity rank indicators, architectural analysis, topic clouds, and live AI re-analysis triggers.
- **State Management**: Redux Toolkit for UI preferences (view modes, active filters) combined with TanStack Query for server state caching.

### 2. Resilient Redis 7 Multi-Layer Caching
- Configured with `GenericJackson2JsonRedisSerializer` and `JavaTimeModule` for JSON serialization of domain records.
- **`CacheErrorHandler` Fallback**: If Redis becomes temporarily unreachable or suffers deserialization errors, requests automatically fall back to PostgreSQL without failing user calls.

### 3. Automated Gemini 3.5 Flash Lite Integration
- Uses Google's high-efficiency `models/gemini-3.5-flash-lite` model.
- Prompts engineered for deterministic, high-signal architectural summaries and standardized developer category tags.

---

## 🚀 Quickstart Guide

### Prerequisites
- [Docker & Docker Compose](https://docs.docker.com/get-docker/) (Docker Engine 24+ recommended)
- [Git](https://git-scm.com/)

### 1. Clone the Repository
```bash
git clone https://github.com/Akshaysd592/ai-trend-explorer.git
cd ai-trend-explorer
```

### 2. Configure Environment Variables
Copy `.env.example` to `.env` and provide your **Google Gemini API Key**:
```bash
cp .env.example .env
```
Edit `.env`:
```env
GEMINI_API_KEY=your_actual_gemini_api_key_here
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970337336763979244226452948404D6351655468576D5A7134743777217A25432A
```

### 3. Start the Entire Distributed Stack
Run Docker Compose in detached mode:
```bash
docker compose up -d --build
```

### 4. Verify System Status
```bash
docker compose ps
```
All 9 containers should report `Up (healthy)`.

### 5. Access the Platform
- **Frontend Web UI**: [http://localhost:3000](http://localhost:3000)
- **API Gateway**: [http://localhost:8080](http://localhost:8080)
- **Trend Service OpenAPI Docs**: [http://localhost:8080/v3/api-docs/trends](http://localhost:8080/v3/api-docs/trends)

---

## ⚙️ Environment Configuration Matrix

| Variable Name | Default Value | Target Service | Description |
|---|---|---|---|
| `GEMINI_API_KEY` | — *(Required)* | `trend-service`, `ai-analysis-service` | Google Gemini API Key for LLM classification |
| `GEMINI_MODEL` | `models/gemini-3.5-flash-lite` | `trend-service`, `ai-analysis-service` | Selected Gemini model |
| `JWT_SECRET` | *(HMAC-SHA512 256-bit Key)* | `auth-service`, `trend-service` | Shared signing key for JWT token verification |
| `KAFKA_BOOTSTRAP_SERVERS` | `kafka:9092` | All Services | Kafka broker connection string |
| `REDIS_HOST` | `redis` | `trend-service` | Redis container host |
| `REDIS_PORT` | `6379` | `trend-service` | Redis connection port |
| `TREND_POSTGRES_DB` | `trend_db` | `trend-postgres`, `trend-service` | Trend database name |
| `AUTH_POSTGRES_DB` | `auth_db` | `auth-postgres`, `auth-service` | Auth database name |
| `NEXT_PUBLIC_API_URL` | `http://localhost:8080` | `frontend` | Gateway URL accessed by the browser |

---

## 🧪 Testing & Quality Assurance

### Run Backend Unit & Integration Tests
```bash
# Test all microservices
./gradlew test

# Or run tests for a specific service
./gradlew :trend-service:test
./gradlew :auth-service:test
./gradlew :ai-analysis-service:test
./gradlew :api-gateway:test
```

### Run Frontend Linting & Build Checks
```bash
cd frontend
npm ci
npm run lint
npm run build
```

---

## 🔁 CI/CD Pipeline

The project includes an enterprise **GitHub Actions** CI workflow (`.github/workflows/ci.yml`) that validates every Pull Request and commit to `main`:

```mermaid
flowchart LR
    A[Push / PR] --> B[Backend CI Matrix<br/>Java 21 / Gradle Test]
    A --> C[Frontend CI<br/>Node 20 / Lint / Build]
    B --> D[Docker Build Validation<br/>5 Multi-Stage Images]
    C --> D
```

- **Backend CI Matrix**: Parallel build and unit testing across all 4 Spring Boot microservices with Gradle build caching.
- **Frontend CI**: TypeScript verification, ESLint auditing, and Next.js standalone build generation.
- **Docker Validation**: Multi-stage image build checks for all services.

---

## 👨‍💻 Author & Maintainer

**Akshay**
- GitHub: [@Akshaysd592](https://github.com/Akshaysd592)
- Repository: [ai-trend-explorer](https://github.com/Akshaysd592/ai-trend-explorer)

---

## 📄 License

This project is licensed under the **MIT License** — Copyright © 2026 **Akshay** ([@Akshaysd592](https://github.com/Akshaysd592)).

See the full [LICENSE](LICENSE) file for terms of use and distribution permissions.
