# Sales Module

## Overview

Retail and wholesale sales with promotion engine, mixed payments, FIFO/FEFO batch allocation, returns/refunds, and POS offline sync.

## API Endpoints

### Sales (`/api/v1/sales`)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/invoices` | Create and confirm sale |
| GET | `/invoices/{id}` | Get invoice |
| POST | `/returns` | Process sales return/refund |

### Promotions (`/api/v1/promotions`)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | List active promotions |
| GET | `/{id}` | Get promotion |
| POST | `/` | Create promotion with rules |
| DELETE | `/{id}` | Deactivate promotion |

### POS Sync (`/api/v1/pos/sync`)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/` | Queue and process POS payload |

## Promotion Engine

Supports `PERCENT_OFF`, `FIXED_OFF`, `MIN_AMOUNT_OFF`, and `BUY_X_GET_Y` types. Rules may target specific products, minimum quantities, or cart amounts. Non-stackable promotions stop further evaluation.

## Sales Flow

1. Build lines from product catalog prices
2. Apply promotions
3. Allocate stock batches (inventory module, expiry-first)
4. Record mixed payments (cash, card, credit, etc.)
5. Update customer ledger and loyalty when customer is attached

## POS Sync

Accepts JSON payloads with `payloadType=SALE`. Entries are stored in `pos_sync_queue` with status `PENDING` → `PROCESSED` or `FAILED`.

## Database Tables

- `sales_invoices`, `sales_lines`, `sales_line_batches`
- `sales_payments`, `sales_returns`, `sales_return_lines`
- `promotions`, `promotion_rules`
- `pos_sync_queue`
