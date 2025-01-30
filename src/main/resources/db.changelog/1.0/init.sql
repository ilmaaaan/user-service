-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id                      BIGSERIAL PRIMARY KEY,
    username                VARCHAR(255) NOT NULL UNIQUE,
    password                VARCHAR(255) NOT NULL,
    email                   VARCHAR(255) NOT NULL UNIQUE,
    name                    VARCHAR(255),
    address                 VARCHAR(255),
    avatar_url              VARCHAR(255),
    account_non_expired     BOOLEAN DEFAULT TRUE,
    account_non_locked      BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    enabled                 BOOLEAN DEFAULT TRUE
    );

-- Таблица ролей
CREATE TABLE IF NOT EXISTS roles (
    role_id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255)
    );

-- Таблица для связи пользователей и ролей (Many-to-Many)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id     BIGINT NOT NULL,
    role_id     BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id)
    REFERENCES roles (role_id) ON DELETE CASCADE
    );

-- Таблица действий пользователя
CREATE TABLE IF NOT EXISTS user_action_logs (
    log_id      BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    actions     JSONB NOT NULL DEFAULT '{}'::jsonb, -- Структурированный журнал действий
    CONSTRAINT fk_user_action_logs_user FOREIGN KEY (log_id)
    REFERENCES users (id) ON DELETE CASCADE
    );