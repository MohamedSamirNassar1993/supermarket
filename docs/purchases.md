# Purchases Module

## Overview

End-to-end procurement workflow: quotation → purchase order → goods receipt → supplier payment/return. Partial receiving updates order status and creates inventory stock batches.

## API Endpoints

Base path: `/api/v1/purchases`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/quotations` | Create purchase quotation (DRAFT) |
| GET | `/quotations/{id}` | Get quotation |
| POST | `/quotations/{id}/approve` | Approve quotation |
| POST | `/quotations/{id}/convert-to-order` | Convert approved quotation to PO |
| POST | `/orders` | Create purchase order |
| GET | `/orders/{id}` | Get purchase order |
| POST | `/orders/{id}/submit` | Submit order for approval |
| POST | `/orders/{id}/approve` | Approve order |
| POST | `/receipts` | Receive goods (partial or full) |
| POST | `/payments` | Record supplier payment |
| POST | `/returns` | Create purchase return |

## Workflow States

`DRAFT` → `SUBMITTED` → `APPROVED` → `PARTIALLY_RECEIVED` / `RECEIVED`

## Goods Receipt

Each receipt line creates a `stock_batches` record via the inventory module (FEFO batch tracking). Order line `received_quantity` is updated; supplier ledger is debited.

## Database Tables

- `purchase_quotations`, `purchase_quotation_lines`
- `purchase_orders`, `purchase_order_lines`
- `goods_receipts`, `goods_receipt_lines`
- `supplier_payments`
- `purchase_returns`, `purchase_return_lines`
