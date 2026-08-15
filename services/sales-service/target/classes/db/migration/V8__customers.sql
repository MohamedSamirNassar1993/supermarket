-- Phase 8: Customers module

CREATE TABLE customer_groups (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    code            VARCHAR(50)    NOT NULL,
    name            VARCHAR(255)   NOT NULL,
    discount_rate   NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    description     TEXT,
    active          BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_customer_groups_org_code UNIQUE (organization_id, code)
);

CREATE INDEX idx_customer_groups_organization_id ON customer_groups (organization_id);

CREATE TABLE customers (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    group_id        UUID           REFERENCES customer_groups (id),
    code            VARCHAR(50)    NOT NULL,
    name            VARCHAR(255)   NOT NULL,
    customer_type   VARCHAR(30)    NOT NULL DEFAULT 'RETAIL',
    email           VARCHAR(255),
    phone           VARCHAR(50),
    address         TEXT,
    tax_id          VARCHAR(100),
    credit_limit    NUMERIC(19, 4) NOT NULL DEFAULT 0,
    current_balance NUMERIC(19, 4) NOT NULL DEFAULT 0,
    loyalty_points  BIGINT         NOT NULL DEFAULT 0,
    active          BOOLEAN        NOT NULL DEFAULT TRUE,
    notes           TEXT,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_customers_org_code UNIQUE (organization_id, code),
    CONSTRAINT chk_customers_type CHECK (customer_type IN ('RETAIL', 'WHOLESALE', 'VIP', 'COMPANY', 'RESTAURANT', 'PHARMACY'))
);

CREATE INDEX idx_customers_organization_id ON customers (organization_id);
CREATE INDEX idx_customers_type ON customers (customer_type);

CREATE TABLE customer_transactions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    customer_id     UUID           NOT NULL REFERENCES customers (id) ON DELETE RESTRICT,
    transaction_type VARCHAR(50)   NOT NULL,
    reference_type  VARCHAR(50),
    reference_id    UUID,
    debit           NUMERIC(19, 4) NOT NULL DEFAULT 0,
    credit          NUMERIC(19, 4) NOT NULL DEFAULT 0,
    balance_after   NUMERIC(19, 4) NOT NULL DEFAULT 0,
    description     TEXT,
    transaction_date DATE         NOT NULL DEFAULT CURRENT_DATE,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT chk_customer_txn_amount CHECK (debit >= 0 AND credit >= 0)
);

CREATE INDEX idx_customer_transactions_customer ON customer_transactions (customer_id, transaction_date DESC);

CREATE TABLE loyalty_transactions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    customer_id     UUID           NOT NULL REFERENCES customers (id) ON DELETE RESTRICT,
    transaction_type VARCHAR(30)   NOT NULL,
    points          BIGINT         NOT NULL,
    balance_after   BIGINT         NOT NULL,
    reference_type  VARCHAR(50),
    reference_id    UUID,
    description     TEXT,
    transaction_date DATE         NOT NULL DEFAULT CURRENT_DATE,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255)
);

CREATE INDEX idx_loyalty_transactions_customer ON loyalty_transactions (customer_id, transaction_date DESC);

CREATE TABLE installments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    customer_id     UUID           NOT NULL REFERENCES customers (id) ON DELETE RESTRICT,
    reference_type  VARCHAR(50)    NOT NULL,
    reference_id    UUID           NOT NULL,
    installment_number INT         NOT NULL,
    due_date        DATE           NOT NULL,
    amount          NUMERIC(19, 4) NOT NULL,
    paid_amount     NUMERIC(19, 4) NOT NULL DEFAULT 0,
    status          VARCHAR(30)    NOT NULL DEFAULT 'PENDING',
    paid_at         TIMESTAMPTZ,
    notes           TEXT,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_installments_ref UNIQUE (reference_type, reference_id, installment_number),
    CONSTRAINT chk_installments_amount CHECK (amount > 0)
);

CREATE INDEX idx_installments_customer ON installments (customer_id, due_date);
CREATE INDEX idx_installments_status ON installments (status);
