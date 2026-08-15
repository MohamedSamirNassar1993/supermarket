-- Phase 14: Async report jobs (RabbitMQ)

CREATE TABLE report_jobs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID         NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    branch_id       UUID         REFERENCES branches (id) ON DELETE SET NULL,
    requested_by    UUID         REFERENCES users (id) ON DELETE SET NULL,
    report_type     VARCHAR(50)  NOT NULL,
    format          VARCHAR(20)  NOT NULL DEFAULT 'PDF',
    parameters      JSONB        NOT NULL DEFAULT '{}',
    status          VARCHAR(30)  NOT NULL DEFAULT 'QUEUED',
    file_path       VARCHAR(2048),
    file_size_bytes BIGINT,
    error_message   TEXT,
    started_at      TIMESTAMPTZ,
    completed_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_report_jobs_format CHECK (format IN ('PDF', 'XLSX', 'CSV')),
    CONSTRAINT chk_report_jobs_status CHECK (status IN ('QUEUED', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED'))
);

CREATE INDEX idx_report_jobs_org_status ON report_jobs (organization_id, status);
CREATE INDEX idx_report_jobs_requested_by ON report_jobs (requested_by, created_at DESC);
