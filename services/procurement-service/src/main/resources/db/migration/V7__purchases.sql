-- Phase 7: Purchases module

CREATE TABLE purchase_quotations (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    branch_id       UUID           NOT NULL REFERENCES branches (id) ON DELETE CASCADE,
    supplier_id     UUID           NOT NULL REFERENCES suppliers (id) ON DELETE RESTRICT,
    quotation_number VARCHAR(50)   NOT NULL,
    status          VARCHAR(30)    NOT NULL DEFAULT 'DRAFT',
    quotation_date  DATE           NOT NULL DEFAULT CURRENT_DATE,
    valid_until     DATE,
    currency_code   CHAR(3)        NOT NULL DEFAULT 'USD',
    subtotal        NUMERIC(19, 4) NOT NULL DEFAULT 0,
    tax_amount      NUMERIC(19, 4) NOT NULL DEFAULT 0,
    total_amount    NUMERIC(19, 4) NOT NULL DEFAULT 0,
    notes           TEXT,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_purchase_quotations_number UNIQUE (organization_id, quotation_number)
);

CREATE INDEX idx_purchase_quotations_supplier ON purchase_quotations (supplier_id);
CREATE INDEX idx_purchase_quotations_status ON purchase_quotations (status);

CREATE TABLE purchase_quotation_lines (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quotation_id    UUID           NOT NULL REFERENCES purchase_quotations (id) ON DELETE CASCADE,
    product_id      UUID           NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    line_number     INT            NOT NULL,
    quantity        NUMERIC(19, 4) NOT NULL,
    unit_price      NUMERIC(19, 4) NOT NULL,
    tax_rate        NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    line_total      NUMERIC(19, 4) NOT NULL DEFAULT 0,
    notes           TEXT,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_purchase_quotation_lines UNIQUE (quotation_id, line_number),
    CONSTRAINT chk_purchase_quotation_lines_qty CHECK (quantity > 0)
);

CREATE TABLE purchase_orders (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    branch_id       UUID           NOT NULL REFERENCES branches (id) ON DELETE CASCADE,
    supplier_id     UUID           NOT NULL REFERENCES suppliers (id) ON DELETE RESTRICT,
    quotation_id    UUID           REFERENCES purchase_quotations (id),
    order_number    VARCHAR(50)    NOT NULL,
    status          VARCHAR(30)    NOT NULL DEFAULT 'DRAFT',
    order_date      DATE           NOT NULL DEFAULT CURRENT_DATE,
    expected_date   DATE,
    approved_at     TIMESTAMPTZ,
    approved_by     VARCHAR(255),
    currency_code   CHAR(3)        NOT NULL DEFAULT 'USD',
    subtotal        NUMERIC(19, 4) NOT NULL DEFAULT 0,
    tax_amount      NUMERIC(19, 4) NOT NULL DEFAULT 0,
    total_amount    NUMERIC(19, 4) NOT NULL DEFAULT 0,
    received_amount NUMERIC(19, 4) NOT NULL DEFAULT 0,
    notes           TEXT,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_purchase_orders_number UNIQUE (organization_id, order_number)
);

CREATE INDEX idx_purchase_orders_supplier ON purchase_orders (supplier_id);
CREATE INDEX idx_purchase_orders_status ON purchase_orders (status);

CREATE TABLE purchase_order_lines (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id        UUID           NOT NULL REFERENCES purchase_orders (id) ON DELETE CASCADE,
    product_id      UUID           NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    line_number     INT            NOT NULL,
    ordered_quantity NUMERIC(19, 4) NOT NULL,
    received_quantity NUMERIC(19, 4) NOT NULL DEFAULT 0,
    unit_price      NUMERIC(19, 4) NOT NULL,
    tax_rate        NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    line_total      NUMERIC(19, 4) NOT NULL DEFAULT 0,
    notes           TEXT,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_purchase_order_lines UNIQUE (order_id, line_number),
    CONSTRAINT chk_purchase_order_lines_qty CHECK (ordered_quantity > 0)
);

CREATE TABLE goods_receipts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    branch_id       UUID           NOT NULL REFERENCES branches (id) ON DELETE CASCADE,
    order_id        UUID           NOT NULL REFERENCES purchase_orders (id) ON DELETE RESTRICT,
    receipt_number  VARCHAR(50)    NOT NULL,
    status          VARCHAR(30)    NOT NULL DEFAULT 'DRAFT',
    receipt_date    DATE           NOT NULL DEFAULT CURRENT_DATE,
    notes           TEXT,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_goods_receipts_number UNIQUE (organization_id, receipt_number)
);

CREATE INDEX idx_goods_receipts_order ON goods_receipts (order_id);

CREATE TABLE goods_receipt_lines (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    receipt_id      UUID           NOT NULL REFERENCES goods_receipts (id) ON DELETE CASCADE,
    order_line_id   UUID           NOT NULL REFERENCES purchase_order_lines (id) ON DELETE RESTRICT,
    product_id      UUID           NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    line_number     INT            NOT NULL,
    received_quantity NUMERIC(19, 4) NOT NULL,
    unit_cost       NUMERIC(19, 4) NOT NULL,
    batch_number    VARCHAR(100),
    expiry_date     DATE,
    stock_batch_id  UUID           REFERENCES stock_batches (id),
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_goods_receipt_lines UNIQUE (receipt_id, line_number),
    CONSTRAINT chk_goods_receipt_lines_qty CHECK (received_quantity > 0)
);

CREATE TABLE supplier_payments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    supplier_id     UUID           NOT NULL REFERENCES suppliers (id) ON DELETE RESTRICT,
    order_id        UUID           REFERENCES purchase_orders (id),
    payment_number  VARCHAR(50)    NOT NULL,
    payment_date    DATE           NOT NULL DEFAULT CURRENT_DATE,
    amount          NUMERIC(19, 4) NOT NULL,
    payment_method  VARCHAR(50)    NOT NULL,
    reference       VARCHAR(255),
    notes           TEXT,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_supplier_payments_number UNIQUE (organization_id, payment_number),
    CONSTRAINT chk_supplier_payments_amount CHECK (amount > 0)
);

CREATE INDEX idx_supplier_payments_supplier ON supplier_payments (supplier_id);

CREATE TABLE purchase_returns (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    branch_id       UUID           NOT NULL REFERENCES branches (id) ON DELETE CASCADE,
    supplier_id     UUID           NOT NULL REFERENCES suppliers (id) ON DELETE RESTRICT,
    order_id        UUID           REFERENCES purchase_orders (id),
    return_number   VARCHAR(50)    NOT NULL,
    status          VARCHAR(30)    NOT NULL DEFAULT 'DRAFT',
    return_date     DATE           NOT NULL DEFAULT CURRENT_DATE,
    reason          TEXT,
    total_amount    NUMERIC(19, 4) NOT NULL DEFAULT 0,
    notes           TEXT,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_purchase_returns_number UNIQUE (organization_id, return_number)
);

CREATE INDEX idx_purchase_returns_supplier ON purchase_returns (supplier_id);

CREATE TABLE purchase_return_lines (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    return_id       UUID           NOT NULL REFERENCES purchase_returns (id) ON DELETE CASCADE,
    product_id      UUID           NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    line_number     INT            NOT NULL,
    quantity        NUMERIC(19, 4) NOT NULL,
    unit_price      NUMERIC(19, 4) NOT NULL,
    line_total      NUMERIC(19, 4) NOT NULL DEFAULT 0,
    stock_batch_id  UUID           REFERENCES stock_batches (id),
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_purchase_return_lines UNIQUE (return_id, line_number),
    CONSTRAINT chk_purchase_return_lines_qty CHECK (quantity > 0)
);
