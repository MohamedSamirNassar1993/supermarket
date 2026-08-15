-- Platform foundation schema for Supermarket ERP

CREATE TABLE organizations (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code            VARCHAR(50)  NOT NULL,
    name            VARCHAR(255) NOT NULL,
    legal_name      VARCHAR(255),
    tax_id          VARCHAR(100),
    email           VARCHAR(255),
    phone           VARCHAR(50),
    address         TEXT,
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_organizations_code UNIQUE (code)
);

CREATE TABLE branches (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID         NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    code            VARCHAR(50)  NOT NULL,
    name            VARCHAR(255) NOT NULL,
    address         TEXT,
    phone           VARCHAR(50),
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_branches_org_code UNIQUE (organization_id, code)
);

CREATE INDEX idx_branches_organization_id ON branches (organization_id);

CREATE TABLE audit_logs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID,
    branch_id       UUID,
    entity_type     VARCHAR(100) NOT NULL,
    entity_id       UUID,
    action          VARCHAR(50)  NOT NULL,
    actor_id        VARCHAR(255),
    actor_name      VARCHAR(255),
    changes         JSONB,
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    occurred_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_logs_organization_id ON audit_logs (organization_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs (entity_type, entity_id);
CREATE INDEX idx_audit_logs_occurred_at ON audit_logs (occurred_at DESC);

CREATE TABLE currencies (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code            CHAR(3)      NOT NULL,
    name            VARCHAR(100) NOT NULL,
    symbol          VARCHAR(10),
    decimal_places  SMALLINT     NOT NULL DEFAULT 2,
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_currencies_code UNIQUE (code)
);

CREATE TABLE exchange_rates (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_currency_id UUID        NOT NULL REFERENCES currencies (id),
    to_currency_id   UUID        NOT NULL REFERENCES currencies (id),
    rate            NUMERIC(19, 8) NOT NULL,
    effective_date  DATE         NOT NULL,
    source          VARCHAR(100),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_exchange_rates_pair_date UNIQUE (from_currency_id, to_currency_id, effective_date),
    CONSTRAINT chk_exchange_rates_different_currencies CHECK (from_currency_id <> to_currency_id),
    CONSTRAINT chk_exchange_rates_positive_rate CHECK (rate > 0)
);

CREATE INDEX idx_exchange_rates_effective_date ON exchange_rates (effective_date DESC);
