-- Phase 5: Inventory, batch tracking, and COGS valuation

CREATE TABLE organization_settings (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id         UUID         NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    valuation_method        VARCHAR(30)  NOT NULL DEFAULT 'FIFO',
    allow_negative_stock    BOOLEAN      NOT NULL DEFAULT FALSE,
    low_stock_alert_enabled BOOLEAN      NOT NULL DEFAULT TRUE,
    expiry_alert_days       INTEGER      NOT NULL DEFAULT 30,
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255),
    CONSTRAINT uq_organization_settings_org UNIQUE (organization_id),
    CONSTRAINT chk_organization_settings_valuation CHECK (valuation_method IN ('FIFO', 'LIFO', 'WEIGHTED_AVERAGE')),
    CONSTRAINT chk_organization_settings_expiry_days CHECK (expiry_alert_days > 0)
);

CREATE TABLE stock_batches (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id    UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    warehouse_id       UUID           NOT NULL REFERENCES warehouses (id) ON DELETE RESTRICT,
    product_id         UUID           NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    variant_id         UUID           REFERENCES product_variants (id) ON DELETE RESTRICT,
    batch_number       VARCHAR(100)   NOT NULL,
    quantity           NUMERIC(19, 4) NOT NULL,
    remaining_quantity NUMERIC(19, 4) NOT NULL,
    unit_cost          NUMERIC(19, 4) NOT NULL,
    received_at        TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    expiry_date        DATE,
    source_type        VARCHAR(50),
    source_id          UUID,
    active             BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255),
    CONSTRAINT uq_stock_batches_number UNIQUE (warehouse_id, product_id, variant_id, batch_number),
    CONSTRAINT chk_stock_batches_positive_qty CHECK (quantity > 0),
    CONSTRAINT chk_stock_batches_remaining_qty CHECK (remaining_quantity >= 0 AND remaining_quantity <= quantity),
    CONSTRAINT chk_stock_batches_unit_cost CHECK (unit_cost >= 0)
);

CREATE INDEX idx_stock_batches_warehouse_product ON stock_batches (warehouse_id, product_id);
CREATE INDEX idx_stock_batches_expiry_date ON stock_batches (expiry_date) WHERE expiry_date IS NOT NULL;
CREATE INDEX idx_stock_batches_remaining ON stock_batches (warehouse_id, product_id, remaining_quantity) WHERE remaining_quantity > 0;

CREATE TABLE inventory_movements (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    warehouse_id    UUID           NOT NULL REFERENCES warehouses (id) ON DELETE RESTRICT,
    product_id      UUID           NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    variant_id      UUID           REFERENCES product_variants (id) ON DELETE RESTRICT,
    movement_type   VARCHAR(20)    NOT NULL,
    quantity        NUMERIC(19, 4) NOT NULL,
    unit_cost       NUMERIC(19, 4),
    total_cost      NUMERIC(19, 4),
    reference_type  VARCHAR(50),
    reference_id    UUID,
    notes           TEXT,
    occurred_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT chk_inventory_movements_type CHECK (movement_type IN ('IN', 'OUT', 'ADJUSTMENT', 'TRANSFER_IN', 'TRANSFER_OUT')),
    CONSTRAINT chk_inventory_movements_qty CHECK (quantity <> 0)
);

CREATE INDEX idx_inventory_movements_warehouse ON inventory_movements (warehouse_id, occurred_at DESC);
CREATE INDEX idx_inventory_movements_product ON inventory_movements (product_id, occurred_at DESC);
CREATE INDEX idx_inventory_movements_reference ON inventory_movements (reference_type, reference_id);

CREATE TABLE batch_allocations (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    movement_id UUID           NOT NULL REFERENCES inventory_movements (id) ON DELETE CASCADE,
    batch_id    UUID           NOT NULL REFERENCES stock_batches (id) ON DELETE RESTRICT,
    quantity    NUMERIC(19, 4) NOT NULL,
    unit_cost   NUMERIC(19, 4) NOT NULL,
    total_cost  NUMERIC(19, 4) NOT NULL,
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_batch_allocations_positive_qty CHECK (quantity > 0),
    CONSTRAINT chk_batch_allocations_cost CHECK (unit_cost >= 0 AND total_cost >= 0)
);

CREATE INDEX idx_batch_allocations_movement_id ON batch_allocations (movement_id);
CREATE INDEX idx_batch_allocations_batch_id ON batch_allocations (batch_id);
