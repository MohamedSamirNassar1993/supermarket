# Supermarket ERP — Microservices Architecture (Phase 7)

Phase 7 completes domain extraction from the monolith into independently deployable services. All external traffic enters through the API gateway; services register with Eureka and communicate via OpenFeign for synchronous calls and RabbitMQ for domain events.

## Service ownership matrix

| Service | Port | Eureka ID | Domain ownership | Status |
|---------|------|-----------|------------------|--------|
| eureka-server | 8761 | eureka-server | Service discovery | Active |
| api-gateway | 8080 | api-gateway | Edge routing, CORS, JWT propagation | Active |
| auth-service | 8081 | auth-service | Authentication, users, roles, JWT issuance | Active |
| catalog-service | 8082 | catalog-service | Products, categories, brands, barcodes | Active |
| inventory-service | 8083 | inventory-service | Stock levels, movements, COGS, warehouses | Active |
| procurement-service | 8084 | procurement-service | Purchases, suppliers, receiving | Active |
| sales-service | 8085 | sales-service | POS, invoices, promotions, returns | Active |
| operations-service | 8086 | operations-service | HR, expenses, losses, payroll | Active |
| analytics-service | 8087 | analytics-service | Reports, dashboards, financial, AI | Active |
| platform-service | 8088 | platform-service | Organizations, branches, settings, exchange rates | Active |
| notification-service | 8089 | notification-service | In-app notifications, event consumers | Active |
| monolith | — | — | Removed | Replaced by microservices under `services/` |

## Inter-service communication

### Synchronous (OpenFeign)

| Consumer | Provider | Internal endpoints |
|----------|----------|-------------------|
| sales-service | catalog-service | `GET /api/v1/products/internal/{id}` |
| sales-service | inventory-service | `POST /api/v1/inventory/internal/*` |
| procurement-service | catalog-service | `GET /api/v1/products/internal/{id}` |
| procurement-service | inventory-service | `POST /api/v1/inventory/internal/*` |
| inventory-service | catalog-service | `GET /api/v1/products/internal/**` |

Consumer services use adapter beans (`InventoryService`, `ProductLookupService`) that delegate to Feign clients, preserving existing domain service signatures.

### Asynchronous (RabbitMQ)

| Publisher | Event | Queue |
|-----------|-------|-------|
| auth-service | `UserCreated`, `LoginSuccess` | `platform.events` |
| notification-service | (consumer) | `platform.events` |

Events use `com.supermarket.common.events.DomainEvent` wrapper records from `supermarket-common`.

### Analytics cross-table queries

`analytics-service` retains `JdbcTemplate` queries against the shared PostgreSQL database for dashboard aggregations spanning sales, inventory, and procurement tables. Feign-based aggregation can replace this in a later phase when databases are split.

## Shared infrastructure

| Component | Purpose |
|-----------|---------|
| PostgreSQL | Shared relational store (single DB, schema-per-domain via Flyway on platform-service) |
| Redis | Caching, rate limiting |
| RabbitMQ | Domain events (`platform.events`), report jobs (`report.jobs`) |

## Database migrations

`platform-service` runs all Flyway migrations (`flyway.enabled: true`). Other services connect to the same database with `flyway.enabled: false` and `ddl-auto: validate`.

## Local development

```bash
# Build all modules from repo root
./mvnw.cmd -f pom.xml clean package -DskipTests

# Run micro stack (no monolith)
cd docker
docker compose -f docker-compose.micro.yml --profile micro up --build
```

- Gateway: http://localhost:8080
- Eureka dashboard: http://localhost:8761
- RabbitMQ management: http://localhost:15672
