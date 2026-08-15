-- Phase 9: Sales module

CREATE TABLE promotions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    code            VARCHAR(50)    NOT NULL,
    name            VARCHAR(255)   NOT NULL,
    description     TEXT,
    promotion_type  VARCHAR(30)    NOT NULL,
    start_date      TIMESTAMPTZ    NOT NULL,
    end_date        TIMESTAMPTZ    NOT NULL,
    priority        INT            NOT NULL DEFAULT 0,
    stackable       BOOLEAN        NOT NULL DEFAULT FALSE,
    active          BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_promotions_org_code UNIQUE (organization_id, code),
    CONSTRAINT chk_promotions_dates CHECK (end_date > start_date)
);

CREATE INDEX idx_promotions_active_dates ON promotions (organization_id, active, start_date, end_date);

CREATE TABLE promotion_rules (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    promotion_id    UUID           NOT NULL REFERENCES promotions (id) ON DELETE CASCADE,
    rule_type       VARCHAR(30)    NOT NULL,
    product_id      UUID           REFERENCES products (id),
    min_quantity    NUMERIC(19, 4),
    min_amount      NUMERIC(19, 4),
    discount_type   VARCHAR(20)    NOT NULL,
    discount_value  NUMERIC(19, 4) NOT NULL,
    free_product_id UUID           REFERENCES products (id),
    free_quantity   NUMERIC(19, 4),
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_promotion_rules_promotion ON promotion_rules (promotion_id);

CREATE TABLE sales_invoices (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    branch_id       UUID           NOT NULL REFERENCES branches (id) ON DELETE CASCADE,
    customer_id     UUID           REFERENCES customers (id),
    invoice_number  VARCHAR(50)    NOT NULL,
    sale_type       VARCHAR(20)    NOT NULL DEFAULT 'RETAIL',
    status          VARCHAR(30)    NOT NULL DEFAULT 'DRAFT',
    invoice_date    TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    subtotal        NUMERIC(19, 4) NOT NULL DEFAULT 0,
    discount_amount NUMERIC(19, 4) NOT NULL DEFAULT 0,
    tax_amount      NUMERIC(19, 4) NOT NULL DEFAULT 0,
    total_amount    NUMERIC(19, 4) NOT NULL DEFAULT 0,
    paid_amount     NUMERIC(19, 4) NOT NULL DEFAULT 0,
    loyalty_points_earned BIGINT   NOT NULL DEFAULT 0,
    loyalty_points_redeemed BIGINT NOT NULL DEFAULT 0,
    promotion_ids   UUID[],
    notes           TEXT,
    pos_terminal_id VARCHAR(100),
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_sales_invoices_number UNIQUE (organization_id, invoice_number),
    CONSTRAINT chk_sales_invoices_type CHECK (sale_type IN ('RETAIL', 'WHOLESALE'))
);

CREATE INDEX idx_sales_invoices_branch_date ON sales_invoices (branch_id, invoice_date DESC);
CREATE INDEX idx_sales_invoices_customer ON sales_invoices (customer_id);
CREATE INDEX idx_sales_invoices_status ON sales_invoices (status);

CREATE TABLE sales_lines (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id      UUID           NOT NULL REFERENCES sales_invoices (id) ON DELETE CASCADE,
    product_id      UUID           NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    line_number     INT            NOT NULL,
    quantity        NUMERIC(19, 4) NOT NULL,
    unit_price      NUMERIC(19, 4) NOT NULL,
    discount_amount NUMERIC(19, 4) NOT NULL DEFAULT 0,
    tax_rate        NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    line_total      NUMERIC(19, 4) NOT NULL DEFAULT 0,
    promotion_id    UUID           REFERENCES promotions (id),
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_sales_lines UNIQUE (invoice_id, line_number),
    CONSTRAINT chk_sales_lines_qty CHECK (quantity > 0)
);

CREATE TABLE sales_line_batches (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sales_line_id   UUID           NOT NULL REFERENCES sales_lines (id) ON DELETE CASCADE,
    stock_batch_id  UUID           NOT NULL REFERENCES stock_batches (id) ON DELETE RESTRICT,
    quantity        NUMERIC(19, 4) NOT NULL,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_sales_line_batches_qty CHECK (quantity > 0)
);

CREATE INDEX idx_sales_line_batches_line ON sales_line_batches (sales_line_id);

CREATE TABLE sales_payments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id      UUID           NOT NULL REFERENCES sales_invoices (id) ON DELETE CASCADE,
    payment_method  VARCHAR(30)    NOT NULL,
    amount          NUMERIC(19, 4) NOT NULL,
    reference       VARCHAR(255),
    paid_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_sales_payments_amount CHECK (amount > 0)
);

CREATE INDEX idx_sales_payments_invoice ON sales_payments (invoice_id);

CREATE TABLE sales_returns (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    branch_id       UUID           NOT NULL REFERENCES branches (id) ON DELETE CASCADE,
    invoice_id      UUID           REFERENCES sales_invoices (id),
    customer_id     UUID           REFERENCES customers (id),
    return_number   VARCHAR(50)    NOT NULL,
    status          VARCHAR(30)    NOT NULL DEFAULT 'DRAFT',
    return_date     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    refund_amount   NUMERIC(19, 4) NOT NULL DEFAULT 0,
    refund_method   VARCHAR(30),
    reason          TEXT,
    notes           TEXT,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_sales_returns_number UNIQUE (organization_id, return_number)
);

CREATE INDEX idx_sales_returns_invoice ON sales_returns (invoice_id);

CREATE TABLE sales_return_lines (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    return_id       UUID           NOT NULL REFERENCES sales_returns (id) ON DELETE CASCADE,
    product_id      UUID           NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    sales_line_id   UUID           REFERENCES sales_lines (id),
    line_number     INT            NOT NULL,
    quantity        NUMERIC(19, 4) NOT NULL,
    unit_price      NUMERIC(19, 4) NOT NULL,
    line_total      NUMERIC(19, 4) NOT NULL DEFAULT 0,
    stock_batch_id  UUID           REFERENCES stock_batches (id),
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_sales_return_lines UNIQUE (return_id, line_number),
    CONSTRAINT chk_sales_return_lines_qty CHECK (quantity > 0)
);

CREATE TABLE pos_sync_queue (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    branch_id       UUID           NOT NULL REFERENCES branches (id) ON DELETE CASCADE,
    terminal_id     VARCHAR(100)   NOT NULL,
    payload_type    VARCHAR(50)    NOT NULL,
    payload         JSONB          NOT NULL,
    status          VARCHAR(30)    NOT NULL DEFAULT 'PENDING',
    error_message   TEXT,
    processed_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pos_sync_queue_status ON pos_sync_queue (status, created_at);
CREATE INDEX idx_pos_sync_queue_terminal ON pos_sync_queue (branch_id, terminal_id);
