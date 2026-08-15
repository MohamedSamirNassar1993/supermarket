-- Phase 10: Expenses and losses

CREATE TABLE expense_categories (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID         NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    code            VARCHAR(50)  NOT NULL,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_expense_categories_org_code UNIQUE (organization_id, code)
);

CREATE INDEX idx_expense_categories_org ON expense_categories (organization_id);

CREATE TABLE expenses (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    branch_id       UUID           NOT NULL REFERENCES branches (id) ON DELETE CASCADE,
    category_id     UUID           NOT NULL REFERENCES expense_categories (id) ON DELETE RESTRICT,
    expense_number  VARCHAR(50)    NOT NULL,
    description     TEXT,
    amount          NUMERIC(19, 4) NOT NULL,
    currency_code   CHAR(3)        NOT NULL DEFAULT 'USD',
    expense_date    DATE           NOT NULL DEFAULT CURRENT_DATE,
    status          VARCHAR(30)    NOT NULL DEFAULT 'PENDING',
    payment_method  VARCHAR(30),
    reference       VARCHAR(255),
    approved_by     VARCHAR(255),
    approved_at     TIMESTAMPTZ,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_expenses_number UNIQUE (organization_id, expense_number),
    CONSTRAINT chk_expenses_amount CHECK (amount > 0),
    CONSTRAINT chk_expenses_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'PAID', 'CANCELLED'))
);

CREATE INDEX idx_expenses_branch_date ON expenses (branch_id, expense_date DESC);
CREATE INDEX idx_expenses_category ON expenses (category_id);
CREATE INDEX idx_expenses_status ON expenses (status);

CREATE TABLE loss_records (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    branch_id       UUID           NOT NULL REFERENCES branches (id) ON DELETE CASCADE,
    product_id      UUID           REFERENCES products (id) ON DELETE SET NULL,
    loss_number     VARCHAR(50)    NOT NULL,
    loss_type       VARCHAR(30)    NOT NULL,
    quantity        NUMERIC(19, 4),
    unit_cost       NUMERIC(19, 4) NOT NULL DEFAULT 0,
    total_loss      NUMERIC(19, 4) NOT NULL,
    reason          TEXT,
    loss_date       DATE           NOT NULL DEFAULT CURRENT_DATE,
    status          VARCHAR(30)    NOT NULL DEFAULT 'RECORDED',
    stock_batch_id  UUID           REFERENCES stock_batches (id),
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_loss_records_number UNIQUE (organization_id, loss_number),
    CONSTRAINT chk_loss_records_type CHECK (loss_type IN ('SPOILAGE', 'DAMAGE', 'THEFT', 'EXPIRY', 'OTHER')),
    CONSTRAINT chk_loss_records_total CHECK (total_loss >= 0)
);

CREATE INDEX idx_loss_records_branch_date ON loss_records (branch_id, loss_date DESC);
CREATE INDEX idx_loss_records_product ON loss_records (product_id);
