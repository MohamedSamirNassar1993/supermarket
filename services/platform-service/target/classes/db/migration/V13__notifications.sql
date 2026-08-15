-- Phase 13: Notifications

CREATE TABLE notifications (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID         NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    user_id         UUID         REFERENCES users (id) ON DELETE CASCADE,
    branch_id       UUID         REFERENCES branches (id) ON DELETE SET NULL,
    notification_type VARCHAR(50) NOT NULL,
    title           VARCHAR(255) NOT NULL,
    message         TEXT         NOT NULL,
    severity        VARCHAR(20)  NOT NULL DEFAULT 'INFO',
    read_at         TIMESTAMPTZ,
    action_url      VARCHAR(2048),
    metadata        JSONB,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_notifications_severity CHECK (severity IN ('INFO', 'WARNING', 'ERROR', 'SUCCESS'))
);

CREATE INDEX idx_notifications_user_unread ON notifications (user_id, read_at) WHERE read_at IS NULL;
CREATE INDEX idx_notifications_org_created ON notifications (organization_id, created_at DESC);

CREATE TABLE notification_preferences (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    notification_type VARCHAR(50) NOT NULL,
    email_enabled   BOOLEAN      NOT NULL DEFAULT TRUE,
    push_enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    in_app_enabled  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_notification_preferences_user_type UNIQUE (user_id, notification_type)
);

CREATE INDEX idx_notification_preferences_user ON notification_preferences (user_id);
