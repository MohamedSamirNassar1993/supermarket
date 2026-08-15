-- Phase 11: HR and payroll

CREATE TABLE employees (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    branch_id       UUID           NOT NULL REFERENCES branches (id) ON DELETE CASCADE,
    employee_number VARCHAR(50)    NOT NULL,
    user_id         UUID           REFERENCES users (id) ON DELETE SET NULL,
    first_name      VARCHAR(100)   NOT NULL,
    last_name       VARCHAR(100)   NOT NULL,
    email           VARCHAR(255),
    phone           VARCHAR(50),
    job_title       VARCHAR(100),
    department      VARCHAR(100),
    hire_date       DATE           NOT NULL,
    termination_date DATE,
    base_salary     NUMERIC(19, 4) NOT NULL DEFAULT 0,
    salary_currency CHAR(3)        NOT NULL DEFAULT 'USD',
    employment_type VARCHAR(30)    NOT NULL DEFAULT 'FULL_TIME',
    status          VARCHAR(30)    NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_employees_number UNIQUE (organization_id, employee_number),
    CONSTRAINT chk_employees_status CHECK (status IN ('ACTIVE', 'ON_LEAVE', 'SUSPENDED', 'TERMINATED')),
    CONSTRAINT chk_employees_type CHECK (employment_type IN ('FULL_TIME', 'PART_TIME', 'CONTRACT', 'TEMPORARY'))
);

CREATE INDEX idx_employees_branch ON employees (branch_id);
CREATE INDEX idx_employees_status ON employees (status);

CREATE TABLE attendance (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id     UUID           NOT NULL REFERENCES employees (id) ON DELETE CASCADE,
    branch_id       UUID           NOT NULL REFERENCES branches (id) ON DELETE CASCADE,
    attendance_date DATE           NOT NULL,
    check_in        TIMESTAMPTZ,
    check_out       TIMESTAMPTZ,
    hours_worked    NUMERIC(5, 2),
    status          VARCHAR(30)    NOT NULL DEFAULT 'PRESENT',
    notes           TEXT,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_attendance_employee_date UNIQUE (employee_id, attendance_date),
    CONSTRAINT chk_attendance_status CHECK (status IN ('PRESENT', 'ABSENT', 'LATE', 'HALF_DAY', 'REMOTE'))
);

CREATE INDEX idx_attendance_branch_date ON attendance (branch_id, attendance_date DESC);

CREATE TABLE leave_requests (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id     UUID           NOT NULL REFERENCES employees (id) ON DELETE CASCADE,
    leave_type      VARCHAR(30)    NOT NULL,
    start_date      DATE           NOT NULL,
    end_date        DATE           NOT NULL,
    days_requested  NUMERIC(5, 2)  NOT NULL,
    reason          TEXT,
    status          VARCHAR(30)    NOT NULL DEFAULT 'PENDING',
    approved_by     VARCHAR(255),
    approved_at     TIMESTAMPTZ,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT chk_leave_requests_dates CHECK (end_date >= start_date),
    CONSTRAINT chk_leave_requests_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED'))
);

CREATE INDEX idx_leave_requests_employee ON leave_requests (employee_id);

CREATE TABLE payroll_runs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID           NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
    branch_id       UUID           REFERENCES branches (id) ON DELETE SET NULL,
    run_number      VARCHAR(50)    NOT NULL,
    period_start    DATE           NOT NULL,
    period_end      DATE           NOT NULL,
    pay_date        DATE           NOT NULL,
    status          VARCHAR(30)    NOT NULL DEFAULT 'DRAFT',
    total_gross     NUMERIC(19, 4) NOT NULL DEFAULT 0,
    total_deductions NUMERIC(19, 4) NOT NULL DEFAULT 0,
    total_net       NUMERIC(19, 4) NOT NULL DEFAULT 0,
    processed_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_payroll_runs_number UNIQUE (organization_id, run_number),
    CONSTRAINT chk_payroll_runs_period CHECK (period_end >= period_start),
    CONSTRAINT chk_payroll_runs_status CHECK (status IN ('DRAFT', 'PROCESSING', 'COMPLETED', 'CANCELLED'))
);

CREATE INDEX idx_payroll_runs_org_period ON payroll_runs (organization_id, period_start DESC);

CREATE TABLE payroll_lines (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payroll_run_id  UUID           NOT NULL REFERENCES payroll_runs (id) ON DELETE CASCADE,
    employee_id     UUID           NOT NULL REFERENCES employees (id) ON DELETE RESTRICT,
    base_pay        NUMERIC(19, 4) NOT NULL DEFAULT 0,
    overtime_pay    NUMERIC(19, 4) NOT NULL DEFAULT 0,
    bonuses         NUMERIC(19, 4) NOT NULL DEFAULT 0,
    deductions      NUMERIC(19, 4) NOT NULL DEFAULT 0,
    gross_pay       NUMERIC(19, 4) NOT NULL DEFAULT 0,
    net_pay         NUMERIC(19, 4) NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_payroll_lines_run_employee UNIQUE (payroll_run_id, employee_id)
);

CREATE INDEX idx_payroll_lines_employee ON payroll_lines (employee_id);

CREATE TABLE salary_advances (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id     UUID           NOT NULL REFERENCES employees (id) ON DELETE CASCADE,
    advance_number  VARCHAR(50)    NOT NULL,
    amount          NUMERIC(19, 4) NOT NULL,
    advance_date    DATE           NOT NULL DEFAULT CURRENT_DATE,
    repayment_months INT           NOT NULL DEFAULT 1,
    monthly_deduction NUMERIC(19, 4) NOT NULL,
    remaining_balance NUMERIC(19, 4) NOT NULL,
    status          VARCHAR(30)    NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_salary_advances_number UNIQUE (employee_id, advance_number),
    CONSTRAINT chk_salary_advances_amount CHECK (amount > 0),
    CONSTRAINT chk_salary_advances_status CHECK (status IN ('ACTIVE', 'REPAID', 'CANCELLED'))
);

CREATE TABLE employee_loans (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id     UUID           NOT NULL REFERENCES employees (id) ON DELETE CASCADE,
    loan_number     VARCHAR(50)    NOT NULL,
    principal       NUMERIC(19, 4) NOT NULL,
    interest_rate   NUMERIC(7, 4)  NOT NULL DEFAULT 0,
    term_months     INT            NOT NULL,
    monthly_payment NUMERIC(19, 4) NOT NULL,
    remaining_balance NUMERIC(19, 4) NOT NULL,
    start_date      DATE           NOT NULL,
    status          VARCHAR(30)    NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_employee_loans_number UNIQUE (employee_id, loan_number),
    CONSTRAINT chk_employee_loans_principal CHECK (principal > 0),
    CONSTRAINT chk_employee_loans_status CHECK (status IN ('ACTIVE', 'PAID_OFF', 'DEFAULTED', 'CANCELLED'))
);
