-- Phase 4: Warehouse operations schema

CREATE TABLE warehouses (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID         NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    branch_id       UUID         NOT NULL REFERENCES branches (id) ON DELETE RESTRICT,
    code            VARCHAR(50)  NOT NULL,
    name            VARCHAR(255) NOT NULL,
    address         TEXT,
    warehouse_type  VARCHAR(30)  NOT NULL DEFAULT 'STORAGE',
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_warehouses_org_code UNIQUE (organization_id, code)
);

CREATE INDEX idx_warehouses_organization_id ON warehouses (organization_id);
CREATE INDEX idx_warehouses_branch_id ON warehouses (branch_id);

CREATE TABLE warehouse_transfers (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id    UUID         NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    from_warehouse_id  UUID         NOT NULL REFERENCES warehouses (id) ON DELETE RESTRICT,
    to_warehouse_id    UUID         NOT NULL REFERENCES warehouses (id) ON DELETE RESTRICT,
    transfer_number    VARCHAR(50)  NOT NULL,
    status             VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    notes              TEXT,
    shipped_at         TIMESTAMPTZ,
    received_at        TIMESTAMPTZ,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255),
    CONSTRAINT uq_warehouse_transfers_number UNIQUE (organization_id, transfer_number),
    CONSTRAINT chk_warehouse_transfers_different CHECK (from_warehouse_id <> to_warehouse_id),
    CONSTRAINT chk_warehouse_transfers_status CHECK (status IN ('DRAFT', 'IN_TRANSIT', 'RECEIVED', 'CANCELLED'))
);

CREATE INDEX idx_warehouse_transfers_organization_id ON warehouse_transfers (organization_id);
CREATE INDEX idx_warehouse_transfers_status ON warehouse_transfers (status);

CREATE TABLE transfer_lines (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transfer_id        UUID           NOT NULL REFERENCES warehouse_transfers (id) ON DELETE CASCADE,
    product_id         UUID           NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    variant_id         UUID           REFERENCES product_variants (id) ON DELETE RESTRICT,
    quantity           NUMERIC(19, 4) NOT NULL,
    received_quantity  NUMERIC(19, 4) NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_transfer_lines_positive_qty CHECK (quantity > 0),
    CONSTRAINT chk_transfer_lines_received_qty CHECK (received_quantity >= 0 AND received_quantity <= quantity)
);

CREATE INDEX idx_transfer_lines_transfer_id ON transfer_lines (transfer_id);

CREATE TABLE stock_counts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID         NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    warehouse_id    UUID         NOT NULL REFERENCES warehouses (id) ON DELETE RESTRICT,
    count_number    VARCHAR(50)  NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    notes           TEXT,
    counted_at      TIMESTAMPTZ,
    completed_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_stock_counts_number UNIQUE (organization_id, count_number),
    CONSTRAINT chk_stock_counts_status CHECK (status IN ('DRAFT', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
);

CREATE INDEX idx_stock_counts_warehouse_id ON stock_counts (warehouse_id);
CREATE INDEX idx_stock_counts_status ON stock_counts (status);

CREATE TABLE stock_count_lines (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    stock_count_id   UUID           NOT NULL REFERENCES stock_counts (id) ON DELETE CASCADE,
    product_id       UUID           NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    variant_id       UUID           REFERENCES product_variants (id) ON DELETE RESTRICT,
    system_quantity  NUMERIC(19, 4) NOT NULL DEFAULT 0,
    counted_quantity NUMERIC(19, 4),
    variance         NUMERIC(19, 4),
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_stock_count_lines_item UNIQUE (stock_count_id, product_id, variant_id)
);

CREATE INDEX idx_stock_count_lines_stock_count_id ON stock_count_lines (stock_count_id);

CREATE TABLE inventory_adjustments (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id   UUID         NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    warehouse_id      UUID         NOT NULL REFERENCES warehouses (id) ON DELETE RESTRICT,
    adjustment_number VARCHAR(50)  NOT NULL,
    reason            VARCHAR(255) NOT NULL,
    status            VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    notes             TEXT,
    applied_at        TIMESTAMPTZ,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    CONSTRAINT uq_inventory_adjustments_number UNIQUE (organization_id, adjustment_number),
    CONSTRAINT chk_inventory_adjustments_status CHECK (status IN ('DRAFT', 'APPLIED', 'CANCELLED'))
);

CREATE INDEX idx_inventory_adjustments_warehouse_id ON inventory_adjustments (warehouse_id);
CREATE INDEX idx_inventory_adjustments_status ON inventory_adjustments (status);

CREATE TABLE inventory_adjustment_lines (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    adjustment_id   UUID           NOT NULL REFERENCES inventory_adjustments (id) ON DELETE CASCADE,
    product_id      UUID           NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    variant_id      UUID           REFERENCES product_variants (id) ON DELETE RESTRICT,
    quantity_change NUMERIC(19, 4) NOT NULL,
    unit_cost       NUMERIC(19, 4),
    notes           TEXT,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_adjustment_lines_non_zero CHECK (quantity_change <> 0)
);

CREATE INDEX idx_inventory_adjustment_lines_adjustment_id ON inventory_adjustment_lines (adjustment_id);
