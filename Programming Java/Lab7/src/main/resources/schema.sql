CREATE TABLE IF NOT EXISTS app_user (
                                        id            BIGSERIAL PRIMARY KEY,
                                        login         TEXT UNIQUE NOT NULL,
                                        pass_md2_hex  CHAR(32)    NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
    );

CREATE SEQUENCE IF NOT EXISTS organization_id_seq START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS organization (
                                            id               BIGINT PRIMARY KEY DEFAULT nextval('organization_id_seq'),
    name             TEXT    NOT NULL,
    x                INTEGER NOT NULL,
    y                BIGINT  NOT NULL,
    annual_turnover  INTEGER,
    employees_count  INTEGER NOT NULL CHECK (employees_count > 0),
    type             TEXT    NOT NULL CHECK (type IN ('COMMERCIAL','PUBLIC','PRIVATE_LIMITED_COMPANY')),
    street           TEXT,
    zip_code         TEXT    NOT NULL CHECK (length(zip_code) <= 18),
    creation_date    TIMESTAMPTZ NOT NULL DEFAULT now(),
    owner_id         BIGINT  NOT NULL REFERENCES app_user(id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_org_owner ON organization(owner_id);
