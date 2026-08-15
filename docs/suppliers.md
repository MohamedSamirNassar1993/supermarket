# Suppliers Module

## Overview

The suppliers module manages vendor master data, account balances, transaction statements, and performance ratings.

## API Endpoints

Base path: `/api/v1/suppliers`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | List suppliers (paginated) |
| GET | `/{id}` | Get supplier by ID |
| POST | `/` | Create supplier |
| PUT | `/{id}` | Update supplier |
| DELETE | `/{id}` | Deactivate supplier |
| GET | `/{id}/balance` | Current balance and available credit |
| GET | `/{id}/statement` | Paginated transaction statement |
| PUT | `/{id}/rating` | Update supplier rating (0–5) |

## Headers

- `X-Organization-Id` (required): Tenant organization UUID
- `X-Actor` (optional): User identifier for audit

## Business Rules

- Supplier codes are unique per organization and stored uppercase.
- Balance is derived from the latest `supplier_transactions.balance_after`.
- Purchases create debit entries; payments and returns create credit entries.
- Deactivation is soft (`active = false`).

## Database Tables

- `suppliers`
- `supplier_transactions`
