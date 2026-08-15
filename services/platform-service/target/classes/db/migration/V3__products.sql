-- Phase 3: Product catalog schema

CREATE TABLE categories (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID         NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    parent_id       UUID         REFERENCES categories (id) ON DELETE SET NULL,
    code            VARCHAR(50)  NOT NULL,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    markup_percent  NUMERIC(7, 4),
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_categories_org_code UNIQUE (organization_id, code)
);

CREATE INDEX idx_categories_organization_id ON categories (organization_id);
CREATE INDEX idx_categories_parent_id ON categories (parent_id);

CREATE TABLE brands (
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
    CONSTRAINT uq_brands_org_code UNIQUE (organization_id, code)
);

CREATE INDEX idx_brands_organization_id ON brands (organization_id);

CREATE TABLE units (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id   UUID         NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    code              VARCHAR(20)  NOT NULL,
    name              VARCHAR(100) NOT NULL,
    symbol            VARCHAR(10),
    base_unit_id      UUID         REFERENCES units (id) ON DELETE SET NULL,
    conversion_factor NUMERIC(19, 6) DEFAULT 1,
    active            BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    CONSTRAINT uq_units_org_code UNIQUE (organization_id, code),
    CONSTRAINT chk_units_positive_conversion CHECK (conversion_factor IS NULL OR conversion_factor > 0)
);

CREATE INDEX idx_units_organization_id ON units (organization_id);

CREATE TABLE products (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id  UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    category_id      UUID           REFERENCES categories (id) ON DELETE SET NULL,
    brand_id         UUID           REFERENCES brands (id) ON DELETE SET NULL,
    unit_id          UUID           NOT NULL REFERENCES units (id),
    sku              VARCHAR(100)   NOT NULL,
    name             VARCHAR(255)   NOT NULL,
    description      TEXT,
    base_price       NUMERIC(19, 4) NOT NULL DEFAULT 0,
    cost_price       NUMERIC(19, 4) NOT NULL DEFAULT 0,
    tax_rate         NUMERIC(7, 4)  NOT NULL DEFAULT 0,
    track_inventory  BOOLEAN        NOT NULL DEFAULT TRUE,
    track_expiry     BOOLEAN        NOT NULL DEFAULT FALSE,
    reorder_level    NUMERIC(19, 4),
    reorder_quantity NUMERIC(19, 4),
    active           BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    CONSTRAINT uq_products_org_sku UNIQUE (organization_id, sku),
    CONSTRAINT chk_products_non_negative_prices CHECK (base_price >= 0 AND cost_price >= 0),
    CONSTRAINT chk_products_tax_rate CHECK (tax_rate >= 0)
);

CREATE INDEX idx_products_organization_id ON products (organization_id);
CREATE INDEX idx_products_category_id ON products (category_id);
CREATE INDEX idx_products_brand_id ON products (brand_id);
CREATE INDEX idx_products_name ON products (organization_id, name);

CREATE TABLE product_variants (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id  UUID           NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    sku         VARCHAR(100)   NOT NULL,
    name        VARCHAR(255)   NOT NULL,
    attributes  JSONB,
    price       NUMERIC(19, 4),
    cost_price  NUMERIC(19, 4),
    active      BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    CONSTRAINT uq_product_variants_product_sku UNIQUE (product_id, sku)
);

CREATE INDEX idx_product_variants_product_id ON product_variants (product_id);

CREATE TABLE product_images (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id  UUID         NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    variant_id  UUID         REFERENCES product_variants (id) ON DELETE CASCADE,
    url         VARCHAR(2048) NOT NULL,
    sort_order  INTEGER      NOT NULL DEFAULT 0,
    is_primary  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255)
);

CREATE INDEX idx_product_images_product_id ON product_images (product_id);

CREATE TABLE price_history (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id    UUID           NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    variant_id    UUID           REFERENCES product_variants (id) ON DELETE CASCADE,
    old_price     NUMERIC(19, 4) NOT NULL,
    new_price     NUMERIC(19, 4) NOT NULL,
    reason        VARCHAR(255),
    effective_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    changed_by    VARCHAR(255),
    created_at    TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_price_history_product_id ON price_history (product_id);
CREATE INDEX idx_price_history_effective_at ON price_history (effective_at DESC);

CREATE TABLE product_barcodes (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id    UUID         NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    variant_id    UUID         REFERENCES product_variants (id) ON DELETE CASCADE,
    barcode       VARCHAR(100) NOT NULL,
    barcode_type  VARCHAR(20)  NOT NULL DEFAULT 'EAN13',
    is_primary    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255),
    CONSTRAINT uq_product_barcodes_barcode UNIQUE (barcode)
);

CREATE INDEX idx_product_barcodes_product_id ON product_barcodes (product_id);
CREATE INDEX idx_product_barcodes_barcode ON product_barcodes (barcode);
