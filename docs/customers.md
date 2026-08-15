# Customers Module

## Overview

Customer master data with types (retail, wholesale, VIP, company, restaurant, pharmacy), credit limits, loyalty points, installment plans, and account statements.

## API Endpoints

Base path: `/api/v1/customers`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | List customers (paginated) |
| GET | `/{id}` | Get customer |
| POST | `/` | Create customer |
| PUT | `/{id}` | Update customer |
| DELETE | `/{id}` | Deactivate customer |
| GET | `/{id}/statement` | Account statement |
| POST | `/{id}/loyalty/adjust` | Adjust loyalty points |
| POST | `/{id}/installments` | Create installment plan |
| GET | `/groups` | List customer groups |
| POST | `/groups` | Create customer group |

## Customer Types

`RETAIL`, `WHOLESALE`, `VIP`, `COMPANY`, `RESTAURANT`, `PHARMACY`

## Credit & Loyalty

- Sales on credit check `current_balance + sale_amount <= credit_limit`.
- Loyalty earn/redeem tracked in `loyalty_transactions`.
- Installments split a reference amount into monthly due records.

## Database Tables

- `customers`, `customer_groups`
- `customer_transactions`
- `loyalty_transactions`
- `installments`
