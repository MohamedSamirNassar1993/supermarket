-- Phase 6: Suppliers module

CREATE TABLE suppliers (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    code            VARCHAR(50)    NOT NULL,
    name            VARCHAR(255)   NOT NULL,
    contact_person  VARCHAR(255),
    email           VARCHAR(255),
    phone           VARCHAR(50),
    address         TEXT,
    tax_id          VARCHAR(100),
    payment_terms   VARCHAR(100),
    credit_limit    NUMERIC(19, 4) NOT NULL DEFAULT 0,
    rating          NUMERIC(3, 2)  NOT NULL DEFAULT 0,
    active          BOOLEAN        NOT NULL DEFAULT TRUE,
    notes           TEXT,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_suppliers_org_code UNIQUE (organization_id, code),
    CONSTRAINT chk_suppliers_rating CHECK (rating >= 0 AND rating <= 5)
);

CREATE INDEX idx_suppliers_organization_id ON suppliers (organization_id);

CREATE TABLE supplier_transactions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    supplier_id     UUID           NOT NULL REFERENCES suppliers (id) ON DELETE RESTRICT,
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
    CONSTRAINT chk_supplier_txn_amount CHECK (debit >= 0 AND credit >= 0)
);

CREATE INDEX idx_supplier_transactions_supplier ON supplier_transactions (supplier_id, transaction_date DESC);
CREATE INDEX idx_supplier_transactions_reference ON supplier_transactions (reference_type, reference_id);
