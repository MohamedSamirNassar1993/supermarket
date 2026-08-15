# Platform Module — Business Rules

Business rules for the platform foundation layer. These rules apply before module-specific logic (products, sales, inventory, etc.) and are enforced at the service/domain layer, not in controllers.

## 1. Multi-Tenancy and Scoping

### BR-PLT-001 — Organization as tenant boundary

Every business customer operates within exactly one **organization**. Cross-organization data access is forbidden. All queries on branch-scoped or transactional data must filter by the authenticated user's `organization_id`.

### BR-PLT-002 — Branch scoping

Operational data (sales, inventory movements, expenses, POS sessions) is scoped to a **branch** within the organization. A user may be assigned to one or more branches; they may only read or mutate data for branches they are authorized to access.

### BR-PLT-003 — Organization code immutability

Once created, an organization **code** cannot be changed. Codes are used in external integrations, reporting exports, and audit trails. Name and contact fields may be updated.

### BR-PLT-004 — Branch code uniqueness

Branch **code** must be unique within its parent organization. The combination `(organization_id, code)` is the business key.

## 2. Organization Lifecycle

### BR-PLT-010 — Organization creation

- `code` is required, uppercase alphanumeric with underscores/hyphens, max 50 characters.
- `name` is required, max 255 characters.
- New organizations are created with `active = true`.
- At least one branch should be created during onboarding (enforced by onboarding workflow in a later phase; not a database constraint).

### BR-PLT-011 — Organization deactivation

- Setting `active = false` prevents new transactions for all branches under the organization.
- Deactivation does not delete data. Historical records remain queryable for reporting and audit.
- Reactivation sets `active = true` and restores ability to create new transactions.
- Hard delete of organizations is not supported.

### BR-PLT-012 — Required contact validation

If `email` is provided, it must be a valid email format. If `taxId` is provided, it must be unique within the deployment (single-tenant deployments may relax this in configuration).

## 3. Branch Lifecycle

### BR-PLT-020 — Branch creation

- A branch must belong to an existing, active organization.
- `code` and `name` are required.
- Default `active = true`.

### BR-PLT-021 — Branch deactivation

- Deactivating a branch blocks new sales, purchases, and inventory movements at that branch.
- Open documents (draft purchase orders, active POS sessions) must be closed or cancelled before deactivation (enforced when those modules exist).
- Deactivated branches remain visible in historical reports.

### BR-PLT-022 — Cascade delete protection

Deleting an organization cascades to its branches at the database level. Application services must never expose hard delete; use deactivation instead.

## 4. Audit Logging

### BR-PLT-030 — Mandatory audit on mutations

Every create, update, delete, approve, cancel, or status-change operation on business entities must write an **audit log** entry containing:

| Field | Rule |
|-------|------|
| `entity_type` | Canonical entity name (e.g. `Organization`, `Branch`, `Product`) |
| `entity_id` | UUID of the affected record |
| `action` | Verb: `CREATE`, `UPDATE`, `DELETE`, `APPROVE`, `CANCEL`, `STATUS_CHANGE` |
| `changes` | JSON object with `before` and/or `after` snapshots of changed fields |
| `actor_id` / `actor_name` | Authenticated user; `SYSTEM` for automated jobs |
| `organization_id` / `branch_id` | Scoped when applicable |
| `ip_address` / `user_agent` | Captured from HTTP request when available |
| `occurred_at` | UTC timestamp of the action |

### BR-PLT-031 — Audit immutability

Audit log records are **append-only**. Updates and deletes to `audit_logs` are forbidden at the application layer.

### BR-PLT-032 — Sensitive field redaction

Passwords, tokens, payment card numbers, and secrets must never appear in the `changes` JSON. Redact with `"[REDACTED]"` before persistence.

### BR-PLT-033 — Audit retention

Audit logs are retained for a minimum of 7 years (configurable per deployment). Archival is handled by operational procedures, not application delete.

## 5. Currency Management

### BR-PLT-040 — ISO 4217 currency codes

Currency `code` must be exactly three uppercase letters matching ISO 4217 (e.g. `USD`, `SAR`, `EGP`). Duplicate codes are rejected.

### BR-PLT-041 — Decimal places

`decimal_places` defaults to 2 and must be between 0 and 4. Amounts stored in transactional modules must be rounded to the currency's decimal places at persistence time.

### BR-PLT-042 — Currency deactivation

Deactivating a currency prevents its use in **new** transactions and exchange rates. Existing historical transactions retain their original currency reference.

### BR-PLT-043 — Base currency (future)

Each organization will designate one **base currency** for consolidated reporting. Until the organization settings migration is applied, reporting defaults to the first active currency configured for the deployment.

## 6. Exchange Rates

### BR-PLT-050 — Positive rates only

Exchange rate values must be strictly greater than zero (`rate > 0`).

### BR-PLT-051 — Distinct currency pair

`from_currency_id` and `to_currency_id` must reference different currencies. Same-currency conversion (rate = 1) is handled in application logic without a stored rate.

### BR-PLT-052 — One rate per pair per day

Only one exchange rate may exist for a given `(from_currency, to_currency, effective_date)` tuple. Updates replace the existing rate for that date (audit logged as UPDATE).

### BR-PLT-053 — Rate lookup

When converting amounts, the system uses the most recent rate where `effective_date <= transaction_date`. If no rate exists, conversion fails with a business error — silent fallback rates are not permitted.

### BR-PLT-054 — Inverse rates

Inverse rates (e.g. USD→SAR and SAR→USD) are stored independently. The system does not auto-compute inverses to avoid rounding inconsistencies. Both directions may be maintained manually or via import.

### BR-PLT-055 — Rate source tracking

The optional `source` field records the origin of the rate (manual entry, central bank feed, import file) for audit and reconciliation.

## 7. API and Response Standards

### BR-PLT-060 — Standard response envelope

All REST endpoints return the `ApiResponse` structure:

```json
{
  "success": true,
  "message": "optional human-readable message",
  "errorCode": "MACHINE_READABLE_CODE",
  "data": {},
  "errors": [{ "field": "code", "message": "must not be blank" }],
  "timestamp": "2026-08-10T17:00:00Z"
}
```

### BR-PLT-061 — Pagination

List endpoints accept `page` (0-based), `size` (1–100, default 20), and `sort` (`field,asc|desc`). Response `data` contains `content`, `page`, `size`, `totalElements`, and `totalPages`.

### BR-PLT-062 — Health endpoint is public

`GET /api/v1/health` and `GET /actuator/health` require no authentication. All other platform endpoints require a valid JWT (once authentication is implemented in Phase 1).

## 8. Infrastructure Integration

### BR-PLT-070 — UTC timestamps

All `TIMESTAMPTZ` columns are stored and compared in UTC. Client applications convert to local timezone for display.

### BR-PLT-071 — Flyway schema ownership

Database schema is owned exclusively by Flyway migrations. Hibernate `ddl-auto` is set to `validate` in all profiles. Manual schema changes outside Flyway are prohibited.

### BR-PLT-072 — Redis usage

Redis caches non-authoritative data: permission sets, session metadata, and dashboard aggregates. Cache misses fall through to the database; cache invalidation occurs on relevant mutations.

### BR-PLT-073 — RabbitMQ platform events

Platform lifecycle events (organization created, branch deactivated, exchange rate updated) are published to the `platform.events` queue for downstream consumers (notifications, cache invalidation, analytics).

## 9. Validation Summary

| Entity | Field | Rule |
|--------|-------|------|
| Organization | code | Required, unique, `^[A-Z0-9_-]+$`, max 50 |
| Organization | name | Required, max 255 |
| Branch | code | Required, unique per org, max 50 |
| Branch | name | Required, max 255 |
| Currency | code | Required, unique, `^[A-Z]{3}$` |
| Currency | decimalPlaces | 0–4, default 2 |
| ExchangeRate | rate | Required, > 0 |
| ExchangeRate | effectiveDate | Required, valid date |
| ExchangeRate | from/to | Must differ |

## 10. Error Codes (Platform)

| Code | HTTP | Description |
|------|------|-------------|
| `ORG_NOT_FOUND` | 404 | Organization does not exist |
| `ORG_CODE_EXISTS` | 409 | Organization code already in use |
| `ORG_INACTIVE` | 422 | Operation not allowed on inactive organization |
| `BRANCH_NOT_FOUND` | 404 | Branch does not exist |
| `BRANCH_CODE_EXISTS` | 409 | Branch code already exists in organization |
| `BRANCH_INACTIVE` | 422 | Operation not allowed on inactive branch |
| `CURRENCY_NOT_FOUND` | 404 | Currency does not exist |
| `CURRENCY_CODE_EXISTS` | 409 | Currency code already exists |
| `EXCHANGE_RATE_NOT_FOUND` | 404 | No rate for pair on given date |
| `EXCHANGE_RATE_EXISTS` | 409 | Rate already exists for pair and date |
| `VALIDATION_ERROR` | 400 | Request validation failed |
