# Supermarket ERP

Production-ready enterprise ERP for supermarkets, grocery stores, hypermarkets, wholesale businesses, pharmacies, and multi-branch retail operations.

## Technology Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17, Spring Boot 3.3, Spring Security, JPA, Flyway |
| Frontend | Angular (LTS), Angular Material, TypeScript |
| Database | PostgreSQL 16 |
| Cache | Redis 7 |
| Messaging | RabbitMQ 3 |
| Deployment | Docker, Docker Compose, Nginx |

## Repository Structure

```
Supermarket/
├── libs/supermarket-common/   # Shared Java DTOs, events, JWT utilities
├── services/                  # Microservices (auth, catalog, inventory, …)
├── apps/                      # Micro frontends (shell + domain remotes)
├── docker/                    # Docker Compose, Nginx, environment templates
├── docs/                      # ERD, API contracts, architecture
└── README.md
```

## Microservices + Micro Frontends Stack

The recommended deployment uses Eureka service discovery, Spring Cloud Gateway, and Angular Module Federation.

### Start the micro backend

```bash
cd docker
cp .env.example .env   # if not already done
docker compose -f docker-compose.micro.yml --profile micro up -d --build
```

This starts:

- **Eureka** on port `8761`
- **API Gateway** on port `8080` (all `/api/v1/**` traffic)
- **Platform, Auth, Catalog, Inventory, Procurement, Sales, Operations, Analytics, Notification** services
- **PostgreSQL**, **Redis**, **RabbitMQ**

Flyway migrations run from `platform-service` on first startup.

### Build backend locally (without Docker)

```bash
./mvnw.cmd compile -DskipTests          # Windows
./mvnw compile -DskipTests              # Linux/macOS
```

### Start the MFE shell

See [apps/README.md](apps/README.md) for full workspace setup. Requires **Node.js 20 LTS+**.

```bash
cd apps
npm install
npm run start:auth    # terminal 1 — :4201
npm run start:shell   # terminal 2 — :4200, proxies /api → gateway :8080
```

Start additional remotes (`mf-catalog`, `mf-inventory`, etc.) on ports `4202`–`4207` as needed.

See [docs/architecture/microservices.md](docs/architecture/microservices.md) and [docs/api/gateway-routing.yaml](docs/api/gateway-routing.yaml) for service boundaries and routing.

## Infrastructure Only (Docker Compose)

Use `docker/docker-compose.yml` to start **PostgreSQL**, **Redis**, and **RabbitMQ** when developing services on the host JVM:

```bash
cd docker
cp .env.example .env   # if not already done
docker compose up -d
```

For the full application stack (gateway, domain services, MFE dev servers), use `docker-compose.micro.yml` as described above.

## Environment Variables

All Docker Compose variables are documented in [`docker/.env.example`](docker/.env.example). Key variables:

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_NAME` | `supermarket` | PostgreSQL database name |
| `DB_USER` / `DB_PASSWORD` | `supermarket` | Database credentials |
| `RABBITMQ_USER` / `RABBITMQ_PASSWORD` | `supermarket` | RabbitMQ credentials |
| `JWT_SECRET` | (dev placeholder) | JWT signing key — **change in production** |
| `POSTGRES_HOST_PORT` | `5432` | Host port for PostgreSQL |
| `REDIS_HOST_PORT` | `6379` | Host port for Redis |
| `RABBITMQ_PORT` | `5672` | Host port for RabbitMQ AMQP |
| `RABBITMQ_MANAGEMENT_PORT` | `15672` | Host port for RabbitMQ management UI |

Microservices read infrastructure settings from environment variables when the `docker` profile is active. See each service's `application-docker.yml` under `services/*/src/main/resources/`.

## Documentation

| Document | Description |
|----------|-------------|
| [`docs/erd/platform.md`](docs/erd/platform.md) | Platform entity relationship diagram |
| [`docs/api/platform.yaml`](docs/api/platform.yaml) | OpenAPI contract for health and platform endpoints |
| [`docs/rules/platform.md`](docs/rules/platform.md) | Platform business rules and validation |

## Architecture

The system follows **Domain-Driven Design** and **Hexagonal Architecture (Ports & Adapters)**:

- Business logic lives in domain and application services, never in controllers
- Constructor injection only
- Standardized API responses via `ApiResponse` envelope
- Global exception handling with consistent error codes
- Flyway-managed database migrations
- Audit logging on all mutating operations

## License

Proprietary — commercial product. All rights reserved.
