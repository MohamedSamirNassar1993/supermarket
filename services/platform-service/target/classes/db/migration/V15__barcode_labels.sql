-- Phase 15: Barcode label templates

CREATE TABLE label_templates (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID         NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    code            VARCHAR(50)  NOT NULL,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    label_type      VARCHAR(30)  NOT NULL DEFAULT 'PRODUCT',
    width_mm        NUMERIC(6, 2) NOT NULL DEFAULT 50,
    height_mm       NUMERIC(6, 2) NOT NULL DEFAULT 30,
    template_data   JSONB        NOT NULL DEFAULT '{}',
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_label_templates_org_code UNIQUE (organization_id, code),
    CONSTRAINT chk_label_templates_type CHECK (label_type IN ('PRODUCT', 'SHELF', 'SHIPPING', 'CUSTOM'))
);

CREATE INDEX idx_label_templates_org ON label_templates (organization_id);
