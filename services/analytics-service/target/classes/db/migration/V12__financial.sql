-- Phase 12: Financial snapshots, cash flow, P&L aggregation

CREATE TABLE financial_snapshots (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    branch_id       UUID           REFERENCES branches (id) ON DELETE SET NULL,
    snapshot_date   DATE           NOT NULL,
    period_type     VARCHAR(20)    NOT NULL DEFAULT 'DAILY',
    revenue         NUMERIC(19, 4) NOT NULL DEFAULT 0,
    cogs            NUMERIC(19, 4) NOT NULL DEFAULT 0,
    gross_profit    NUMERIC(19, 4) NOT NULL DEFAULT 0,
    operating_expenses NUMERIC(19, 4) NOT NULL DEFAULT 0,
    net_profit      NUMERIC(19, 4) NOT NULL DEFAULT 0,
    currency_code   CHAR(3)        NOT NULL DEFAULT 'USD',
    metadata        JSONB,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_financial_snapshots UNIQUE (organization_id, branch_id, snapshot_date, period_type),
    CONSTRAINT chk_financial_snapshots_period CHECK (period_type IN ('DAILY', 'WEEKLY', 'MONTHLY', 'QUARTERLY', 'YEARLY'))
);

CREATE INDEX idx_financial_snapshots_org_date ON financial_snapshots (organization_id, snapshot_date DESC);

CREATE TABLE cash_flow_entries (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    branch_id       UUID           REFERENCES branches (id) ON DELETE SET NULL,
    entry_date      DATE           NOT NULL,
    flow_type       VARCHAR(20)    NOT NULL,
    category        VARCHAR(50)    NOT NULL,
    amount          NUMERIC(19, 4) NOT NULL,
    currency_code   CHAR(3)        NOT NULL DEFAULT 'USD',
    reference_type  VARCHAR(50),
    reference_id    UUID,
    description     TEXT,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT chk_cash_flow_type CHECK (flow_type IN ('INFLOW', 'OUTFLOW')),
    CONSTRAINT chk_cash_flow_amount CHECK (amount <> 0)
);

CREATE INDEX idx_cash_flow_entries_org_date ON cash_flow_entries (organization_id, entry_date DESC);
CREATE INDEX idx_cash_flow_entries_branch ON cash_flow_entries (branch_id, entry_date DESC);

-- P&L aggregation view (branch-scoped revenue, COGS)
CREATE OR REPLACE VIEW v_profit_loss AS
WITH invoice_revenue AS (
    SELECT organization_id, branch_id,
           DATE_TRUNC('month', invoice_date)::DATE AS period_start,
           SUM(total_amount) AS revenue
    FROM sales_invoices
    WHERE status IN ('COMPLETED', 'PAID', 'PARTIALLY_PAID')
    GROUP BY organization_id, branch_id, DATE_TRUNC('month', invoice_date)
),
invoice_cogs AS (
    SELECT si.organization_id, si.branch_id,
           DATE_TRUNC('month', si.invoice_date)::DATE AS period_start,
           SUM(slb.quantity * sb.unit_cost) AS cogs
    FROM sales_invoices si
    JOIN sales_lines sl ON sl.invoice_id = si.id
    JOIN sales_line_batches slb ON slb.sales_line_id = sl.id
    JOIN stock_batches sb ON sb.id = slb.stock_batch_id
    WHERE si.status IN ('COMPLETED', 'PAID', 'PARTIALLY_PAID')
    GROUP BY si.organization_id, si.branch_id, DATE_TRUNC('month', si.invoice_date)
)
SELECT
    COALESCE(r.organization_id, c.organization_id) AS organization_id,
    COALESCE(r.branch_id, c.branch_id) AS branch_id,
    COALESCE(r.period_start, c.period_start) AS period_start,
    COALESCE(r.revenue, 0) AS revenue,
    COALESCE(c.cogs, 0) AS cogs,
    COALESCE(r.revenue, 0) - COALESCE(c.cogs, 0) AS gross_profit
FROM invoice_revenue r
FULL OUTER JOIN invoice_cogs c
    ON r.organization_id = c.organization_id
   AND r.branch_id = c.branch_id
   AND r.period_start = c.period_start;

CREATE OR REPLACE VIEW v_expense_summary AS
SELECT
    e.organization_id,
    e.branch_id,
    DATE_TRUNC('month', e.expense_date)::DATE AS period_start,
    ec.code AS category_code,
    ec.name AS category_name,
    SUM(e.amount) FILTER (WHERE e.status IN ('APPROVED', 'PAID')) AS total_amount
FROM expenses e
JOIN expense_categories ec ON ec.id = e.category_id
GROUP BY e.organization_id, e.branch_id, DATE_TRUNC('month', e.expense_date), ec.code, ec.name;

CREATE OR REPLACE VIEW v_loss_summary AS
SELECT
    lr.organization_id,
    lr.branch_id,
    DATE_TRUNC('month', lr.loss_date)::DATE AS period_start,
    lr.loss_type,
    SUM(lr.total_loss) AS total_loss
FROM loss_records lr
WHERE lr.status = 'RECORDED'
GROUP BY lr.organization_id, lr.branch_id, DATE_TRUNC('month', lr.loss_date), lr.loss_type;
