# Warehouse Module — Entity Relationship Diagram

Warehouse operations schema from Flyway migration `V4__warehouse.sql`.

## Diagram

```mermaid
erDiagram
    organizations ||--o{ warehouses : "has"
    branches ||--o{ warehouses : "located_at"
    organizations ||--o{ warehouse_transfers : "has"
    warehouses ||--o{ warehouse_transfers : "from"
    warehouses ||--o{ warehouse_transfers : "to"
    warehouse_transfers ||--o{ transfer_lines : "contains"

    warehouses ||--o{ stock_counts : "counted_in"
    stock_counts ||--o{ stock_count_lines : "contains"
    warehouses ||--o{ inventory_adjustments : "adjusted_in"
    inventory_adjustments ||--o{ inventory_adjustment_lines : "contains"

    products ||--o{ transfer_lines : "item"
    products ||--o{ stock_count_lines : "item"
    products ||--o{ inventory_adjustment_lines : "item"

    warehouses {
        uuid id PK
        uuid organization_id FK
        uuid branch_id FK
        varchar code UK "per org"
        varchar warehouse_type
        boolean active
    }

    warehouse_transfers {
        uuid id PK
        uuid from_warehouse_id FK
        uuid to_warehouse_id FK
        varchar transfer_number UK "per org"
        varchar status "DRAFT|IN_TRANSIT|RECEIVED|CANCELLED"
        timestamptz shipped_at
        timestamptz received_at
    }

    transfer_lines {
        uuid id PK
        uuid transfer_id FK
        uuid product_id FK
        uuid variant_id FK
        numeric quantity
        numeric received_quantity
    }

    stock_counts {
        uuid id PK
        uuid warehouse_id FK
        varchar count_number UK "per org"
        varchar status "DRAFT|IN_PROGRESS|COMPLETED|CANCELLED"
    }

    inventory_adjustments {
        uuid id PK
        uuid warehouse_id FK
        varchar adjustment_number UK "per org"
        varchar status "DRAFT|APPLIED|CANCELLED"
    }
```

## Transfer Lifecycle

```mermaid
stateDiagram-v2
    [*] --> DRAFT
    DRAFT --> IN_TRANSIT : ship (TRANSFER_OUT movements)
    IN_TRANSIT --> RECEIVED : receive (TRANSFER_IN movements)
    DRAFT --> CANCELLED : cancel
```

## Business Rules

- Source and destination warehouses must differ.
- Shipping creates outbound inventory movements at the source warehouse.
- Receiving creates inbound batches at the destination warehouse.
- Stock count completion posts adjustment movements for non-zero variances.
- Applied adjustments create signed inventory movements per line.
