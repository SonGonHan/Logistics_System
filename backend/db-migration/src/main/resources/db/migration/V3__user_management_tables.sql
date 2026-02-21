-- ============================================================================
-- 3. СОЗДАНИЕ ОСНОВНЫХ ТАБЛИЦ user_management
-- ============================================================================

CREATE TABLE user_management.users (
                                       user_id BIGSERIAL PRIMARY KEY,
                                       email VARCHAR(255) UNIQUE,
                                       phone VARCHAR(20) UNIQUE,
                                       password_hash VARCHAR(255),
                                       first_name VARCHAR(100),
                                       last_name VARCHAR(100),
                                       middle_name VARCHAR(100),
                                       role_name VARCHAR(50) NOT NULL DEFAULT 'CLIENT',
                                       facility_id BIGINT,
                                       user_status VARCHAR(20) DEFAULT 'ACTIVE',
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       last_accessed_at TIMESTAMP,

                                       CONSTRAINT check_role_name CHECK (
                                           role_name IN (
                                                         'CLIENT',
                                                         'PVZ_OPERATOR',
                                                         'PVZ_ADMIN',
                                                         'COURIER',
                                                         'DRIVER',
                                                         'DISPATCHER',
                                                         'WAREHOUSE_OPERATOR',
                                                         'WAREHOUSE_ADMIN',
                                                         'HR',
                                                         'ACCOUNTANT',
                                                         'SYSTEM_ADMIN',
                                                         'SYSTEM'
                                               )
                                           ),
                                       CONSTRAINT check_auth_data CHECK (
                                           (role_name IN ('SYSTEM') AND password_hash IS NULL)
                                               OR
                                           (role_name NOT IN ('SYSTEM') AND (email IS NOT NULL OR phone IS NOT NULL))
                                           )
);

COMMENT ON TABLE user_management.users IS 'Единый реестр всех пользователей системы';

CREATE TABLE user_management.user_sessions (
                                               session_id BIGSERIAL PRIMARY KEY,
                                               user_id BIGINT NOT NULL REFERENCES user_management.users(user_id) ON DELETE CASCADE,
                                               refresh_token VARCHAR(255) NOT NULL UNIQUE,
                                               expires_at TIMESTAMP NOT NULL,
                                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                               revoked BOOLEAN DEFAULT FALSE,
                                               ip_address INET,
                                               user_agent TEXT
);

CREATE TABLE user_management.audit_logs (
                                            audit_log_id BIGSERIAL PRIMARY KEY,
                                            user_id BIGINT REFERENCES user_management.users(user_id) ON DELETE SET NULL,
                                            action_type_id SMALLINT NOT NULL REFERENCES shared_data.audit_action_types(action_type_id) ON DELETE RESTRICT,
                                            table_name VARCHAR(100),
                                            record_id BIGINT,
                                            actor_identifier VARCHAR(255),
                                            new_values JSONB,
                                            performed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                            ip_address INET
);

COMMENT ON COLUMN user_management.audit_logs.record_id IS 'ID изменённой записи в связке с table_name';
