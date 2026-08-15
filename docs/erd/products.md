# Products Module — Entity Relationship Diagram

Product catalog schema from Flyway migrations `V3__products.sql`. All entities are scoped to an organization.

## Diagram

```mermaid
erDiagram
    organizations ||--o{ categories : "has"
    organizations ||--o{ brands : "has"
    organizations ||--o{ units : "has"
    organizations ||--o{ products : "has"

    categories ||--o{ categories : "parent"
    categories ||--o{ products : "classifies"
    brands ||--o{ products : "labels"
    units ||--o{ products : "measures"
    units ||--o{ units : "base_unit"

    products ||--o{ product_variants : "has"
    products ||--o{ product_images : "has"
    products ||--o{ product_barcodes : "has"
    products ||--o{ price_history : "tracks"

    product_variants ||--o{ product_images : "optional"
    product_variants ||--o{ product_barcodes : "optional"
    product_variants ||--o{ price_history : "optional"

    categories {
        uuid id PK
        uuid organization_id FK
        uuid parent_id FK
        varchar code UK "per org"
        varchar name
        numeric markup_percent
        boolean active
    }

    brands {
        uuid id PK
        uuid organization_id FK
        varchar code UK "per org"
        varchar name
        boolean active
    }

    units {
        uuid id PK
        uuid organization_id FK
        varchar code UK "per org"
        uuid base_unit_id FK
        numeric conversion_factor
    }

    products {
        uuid id PK
        uuid organization_id FK
        uuid category_id FK
        uuid brand_id FK
        uuid unit_id FK
        varchar sku UK "per org"
        numeric base_price
        numeric cost_price
        numeric reorder_level
        boolean track_inventory
        boolean track_expiry
    }

    product_variants {
        uuid id PK
        uuid product_id FK
        varchar sku UK "per product"
        jsonb attributes
        numeric price
    }

    product_barcodes {
        uuid id PK
        uuid product_id FK
        varchar barcode UK "global"
        varchar barcode_type
    }

    price_history {
        uuid id PK
        uuid product_id FK
        uuid variant_id FK
        numeric old_price
        numeric new_price
        timestamptz effective_at
    }
```

## Key Relationships

| Entity | Platform link |
|--------|---------------|
| Category, Brand, Unit, Product | `organization_id` → `organizations` |
| Product | Optional `category_id`, `brand_id`; required `unit_id` |
| Product variant | `product_id` → `products` (cascade delete) |
| Barcode | Globally unique `barcode`; linked to product and optional variant |

## Business Rules

- SKU is unique per organization; variant SKU is unique per product.
- Barcode is globally unique across all organizations.
- Price changes on products or variants create immutable `price_history` rows.
- Category `markup_percent` drives dynamic pricing when no override is supplied.

## Cross-Module Links

| Consumer module | Reference |
|-----------------|-----------|
| Warehouse transfers | `product_id`, `variant_id` on transfer lines |
| Inventory / batches | `product_id`, `variant_id` on stock batches and movements |
