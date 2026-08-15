-- Authentication and authorization schema for Supermarket ERP

CREATE TABLE roles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code            VARCHAR(50)  NOT NULL,
    name            VARCHAR(100) NOT NULL,
    description     TEXT,
    system_role     BOOLEAN      NOT NULL DEFAULT FALSE,
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_roles_code UNIQUE (code)
);

CREATE TABLE permissions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code            VARCHAR(100) NOT NULL,
    module          VARCHAR(50)  NOT NULL,
    action          VARCHAR(50)  NOT NULL,
    description     TEXT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_permissions_code UNIQUE (code)
);

CREATE INDEX idx_permissions_module ON permissions (module);

CREATE TABLE users (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id     UUID         REFERENCES organizations (id),
    email               VARCHAR(255) NOT NULL,
    username            VARCHAR(100) NOT NULL,
    password_hash       VARCHAR(255) NOT NULL,
    first_name          VARCHAR(100),
    last_name           VARCHAR(100),
    phone               VARCHAR(50),
    active              BOOLEAN      NOT NULL DEFAULT TRUE,
    email_verified      BOOLEAN      NOT NULL DEFAULT FALSE,
    two_factor_enabled  BOOLEAN      NOT NULL DEFAULT FALSE,
    last_login_at       TIMESTAMPTZ,
    password_changed_at TIMESTAMPTZ,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT uq_users_username UNIQUE (username)
);

CREATE INDEX idx_users_organization_id ON users (organization_id);
CREATE INDEX idx_users_active ON users (active);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);

CREATE TABLE role_permissions (
    role_id       UUID NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions (id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE INDEX idx_role_permissions_permission_id ON role_permissions (permission_id);

CREATE TABLE user_branches (
    user_id    UUID    NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    branch_id  UUID    NOT NULL REFERENCES branches (id) ON DELETE CASCADE,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, branch_id)
);

CREATE INDEX idx_user_branches_branch_id ON user_branches (branch_id);

CREATE TABLE refresh_tokens (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token_hash  VARCHAR(255) NOT NULL,
    expires_at  TIMESTAMPTZ  NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    revoked_at  TIMESTAMPTZ,
    ip_address  VARCHAR(45),
    user_agent  TEXT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_refresh_tokens_token_hash UNIQUE (token_hash)
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens (expires_at);

CREATE TABLE login_history (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID REFERENCES users (id) ON DELETE SET NULL,
    email_attempted VARCHAR(255) NOT NULL,
    success         BOOLEAN      NOT NULL,
    failure_reason  VARCHAR(255),
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    occurred_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_login_history_user_id ON login_history (user_id);
CREATE INDEX idx_login_history_occurred_at ON login_history (occurred_at DESC);

-- Default organization and branch for seed users
INSERT INTO organizations (id, code, name, legal_name, active)
VALUES ('00000000-0000-0000-0000-000000000001', 'DEFAULT', 'Default Organization', 'Default Organization LLC', TRUE);

INSERT INTO branches (id, organization_id, code, name, active)
VALUES ('00000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000001', 'MAIN', 'Main Branch', TRUE);

-- System roles
INSERT INTO roles (id, code, name, description, system_role, active) VALUES
    ('10000000-0000-0000-0000-000000000001', 'OWNER', 'Owner', 'Full system ownership with unrestricted access', TRUE, TRUE),
    ('10000000-0000-0000-0000-000000000002', 'ADMINISTRATOR', 'Administrator', 'System administrator with broad management access', TRUE, TRUE),
    ('10000000-0000-0000-0000-000000000003', 'BRANCH_MANAGER', 'Branch Manager', 'Manages branch operations and staff', TRUE, TRUE),
    ('10000000-0000-0000-0000-000000000004', 'ACCOUNTANT', 'Accountant', 'Financial accounting and reporting', TRUE, TRUE),
    ('10000000-0000-0000-0000-000000000005', 'PURCHASING_OFFICER', 'Purchasing Officer', 'Procurement and supplier management', TRUE, TRUE),
    ('10000000-0000-0000-0000-000000000006', 'WAREHOUSE_OFFICER', 'Warehouse Officer', 'Inventory and warehouse operations', TRUE, TRUE),
    ('10000000-0000-0000-0000-000000000007', 'CASHIER', 'Cashier', 'Point of sale and cash handling', TRUE, TRUE),
    ('10000000-0000-0000-0000-000000000008', 'SALES_REPRESENTATIVE', 'Sales Representative', 'Sales orders and customer relations', TRUE, TRUE),
    ('10000000-0000-0000-0000-000000000009', 'HR_OFFICER', 'HR Officer', 'Human resources management', TRUE, TRUE);

-- Permissions ({module}:{action})
INSERT INTO permissions (id, code, module, action, description) VALUES
    ('20000000-0000-0000-0000-000000000001', 'users:read', 'users', 'read', 'View users'),
    ('20000000-0000-0000-0000-000000000002', 'users:create', 'users', 'create', 'Create users'),
    ('20000000-0000-0000-0000-000000000003', 'users:update', 'users', 'update', 'Update users'),
    ('20000000-0000-0000-0000-000000000004', 'users:delete', 'users', 'delete', 'Deactivate or delete users'),
    ('20000000-0000-0000-0000-000000000005', 'roles:read', 'roles', 'read', 'View roles'),
    ('20000000-0000-0000-0000-000000000006', 'roles:create', 'roles', 'create', 'Create roles'),
    ('20000000-0000-0000-0000-000000000007', 'roles:update', 'roles', 'update', 'Update roles'),
    ('20000000-0000-0000-0000-000000000008', 'roles:delete', 'roles', 'delete', 'Delete roles'),
    ('20000000-0000-0000-0000-000000000009', 'organizations:read', 'organizations', 'read', 'View organizations'),
    ('20000000-0000-0000-0000-000000000010', 'organizations:update', 'organizations', 'update', 'Update organizations'),
    ('20000000-0000-0000-0000-000000000011', 'branches:read', 'branches', 'read', 'View branches'),
    ('20000000-0000-0000-0000-000000000012', 'branches:create', 'branches', 'create', 'Create branches'),
    ('20000000-0000-0000-0000-000000000013', 'branches:update', 'branches', 'update', 'Update branches'),
    ('20000000-0000-0000-0000-000000000014', 'branches:delete', 'branches', 'delete', 'Delete branches'),
    ('20000000-0000-0000-0000-000000000015', 'inventory:read', 'inventory', 'read', 'View inventory'),
    ('20000000-0000-0000-0000-000000000016', 'inventory:create', 'inventory', 'create', 'Create inventory records'),
    ('20000000-0000-0000-0000-000000000017', 'inventory:update', 'inventory', 'update', 'Update inventory'),
    ('20000000-0000-0000-0000-000000000018', 'inventory:delete', 'inventory', 'delete', 'Delete inventory records'),
    ('20000000-0000-0000-0000-000000000019', 'sales:read', 'sales', 'read', 'View sales'),
    ('20000000-0000-0000-0000-000000000020', 'sales:create', 'sales', 'create', 'Create sales transactions'),
    ('20000000-0000-0000-0000-000000000021', 'sales:update', 'sales', 'update', 'Update sales'),
    ('20000000-0000-0000-0000-000000000022', 'sales:delete', 'sales', 'delete', 'Cancel or delete sales'),
    ('20000000-0000-0000-0000-000000000023', 'purchases:read', 'purchases', 'read', 'View purchases'),
    ('20000000-0000-0000-0000-000000000024', 'purchases:create', 'purchases', 'create', 'Create purchase orders'),
    ('20000000-0000-0000-0000-000000000025', 'purchases:update', 'purchases', 'update', 'Update purchase orders'),
    ('20000000-0000-0000-0000-000000000026', 'purchases:delete', 'purchases', 'delete', 'Cancel purchase orders'),
    ('20000000-0000-0000-0000-000000000027', 'purchases:approve', 'purchases', 'approve', 'Approve purchase orders'),
    ('20000000-0000-0000-0000-000000000028', 'accounting:read', 'accounting', 'read', 'View accounting data'),
    ('20000000-0000-0000-0000-000000000029', 'accounting:create', 'accounting', 'create', 'Create accounting entries'),
    ('20000000-0000-0000-0000-000000000030', 'accounting:update', 'accounting', 'update', 'Update accounting entries'),
    ('20000000-0000-0000-0000-000000000031', 'warehouse:read', 'warehouse', 'read', 'View warehouse operations'),
    ('20000000-0000-0000-0000-000000000032', 'warehouse:create', 'warehouse', 'create', 'Create warehouse transactions'),
    ('20000000-0000-0000-0000-000000000033', 'warehouse:update', 'warehouse', 'update', 'Update warehouse transactions'),
    ('20000000-0000-0000-0000-000000000034', 'pos:read', 'pos', 'read', 'View POS sessions'),
    ('20000000-0000-0000-0000-000000000035', 'pos:create', 'pos', 'create', 'Process POS transactions'),
    ('20000000-0000-0000-0000-000000000036', 'pos:update', 'pos', 'update', 'Modify POS transactions'),
    ('20000000-0000-0000-0000-000000000037', 'hr:read', 'hr', 'read', 'View HR records'),
    ('20000000-0000-0000-0000-000000000038', 'hr:create', 'hr', 'create', 'Create HR records'),
    ('20000000-0000-0000-0000-000000000039', 'hr:update', 'hr', 'update', 'Update HR records'),
    ('20000000-0000-0000-0000-000000000040', 'hr:delete', 'hr', 'delete', 'Delete HR records'),
    ('20000000-0000-0000-0000-000000000041', 'reports:read', 'reports', 'read', 'View reports'),
    ('20000000-0000-0000-0000-000000000042', 'reports:export', 'reports', 'export', 'Export reports'),
    ('20000000-0000-0000-0000-000000000043', 'settings:read', 'settings', 'read', 'View system settings'),
    ('20000000-0000-0000-0000-000000000044', 'settings:update', 'settings', 'update', 'Update system settings');

-- Owner: all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT '10000000-0000-0000-0000-000000000001', id FROM permissions;

-- Administrator: all except settings:update (owner-only config)
INSERT INTO role_permissions (role_id, permission_id)
SELECT '10000000-0000-0000-0000-000000000002', id FROM permissions
WHERE code <> 'settings:update';

-- Branch Manager
INSERT INTO role_permissions (role_id, permission_id)
SELECT '10000000-0000-0000-0000-000000000003', id FROM permissions
WHERE code IN (
    'users:read', 'branches:read', 'branches:update',
    'inventory:read', 'inventory:update',
    'sales:read', 'sales:create', 'sales:update',
    'purchases:read', 'purchases:create',
    'warehouse:read', 'warehouse:update',
    'pos:read', 'pos:create', 'pos:update',
    'reports:read', 'reports:export'
);

-- Accountant
INSERT INTO role_permissions (role_id, permission_id)
SELECT '10000000-0000-0000-0000-000000000004', id FROM permissions
WHERE code IN (
    'accounting:read', 'accounting:create', 'accounting:update',
    'purchases:read', 'sales:read',
    'reports:read', 'reports:export'
);

-- Purchasing Officer
INSERT INTO role_permissions (role_id, permission_id)
SELECT '10000000-0000-0000-0000-000000000005', id FROM permissions
WHERE code IN (
    'purchases:read', 'purchases:create', 'purchases:update', 'purchases:approve',
    'inventory:read', 'reports:read'
);

-- Warehouse Officer
INSERT INTO role_permissions (role_id, permission_id)
SELECT '10000000-0000-0000-0000-000000000006', id FROM permissions
WHERE code IN (
    'warehouse:read', 'warehouse:create', 'warehouse:update',
    'inventory:read', 'inventory:create', 'inventory:update',
    'purchases:read'
);

-- Cashier
INSERT INTO role_permissions (role_id, permission_id)
SELECT '10000000-0000-0000-0000-000000000007', id FROM permissions
WHERE code IN (
    'pos:read', 'pos:create', 'pos:update',
    'sales:read', 'sales:create',
    'inventory:read'
);

-- Sales Representative
INSERT INTO role_permissions (role_id, permission_id)
SELECT '10000000-0000-0000-0000-000000000008', id FROM permissions
WHERE code IN (
    'sales:read', 'sales:create', 'sales:update',
    'inventory:read', 'reports:read'
);

-- HR Officer
INSERT INTO role_permissions (role_id, permission_id)
SELECT '10000000-0000-0000-0000-000000000009', id FROM permissions
WHERE code IN (
    'hr:read', 'hr:create', 'hr:update', 'hr:delete',
    'users:read', 'users:create', 'users:update',
    'reports:read'
);

-- Default owner user (password: password)
INSERT INTO users (
    id, organization_id, email, username, password_hash,
    first_name, last_name, active, email_verified, password_changed_at
) VALUES (
    '30000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    'owner@supermarket.local',
    'owner',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'System',
    'Owner',
    TRUE,
    TRUE,
    NOW()
);

INSERT INTO user_roles (user_id, role_id)
VALUES ('30000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001');

INSERT INTO user_branches (user_id, branch_id, is_primary)
VALUES ('30000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000010', TRUE);
