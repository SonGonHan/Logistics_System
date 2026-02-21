 -- ============================================================================
-- 2. СОЗДАНИЕ СПРАВОЧНЫХ ТАБЛИЦ В shared_data (БЕЗ FK)
-- ============================================================================

CREATE TABLE shared_data.audit_action_types (
                                                action_type_id SMALLSERIAL PRIMARY KEY,
                                                action_name VARCHAR(100) NOT NULL UNIQUE,
                                                category VARCHAR(50) NOT NULL,
                                                description TEXT
);

CREATE TABLE shared_data.system_settings (
                                             setting_key VARCHAR(100) PRIMARY KEY,
                                             setting_value TEXT NOT NULL,
                                             description TEXT
);

CREATE TABLE shared_data.company_facilities (
                                                facility_id BIGSERIAL PRIMARY KEY,
                                                facility_type VARCHAR(20) NOT NULL,
                                                facility_name VARCHAR(255) NOT NULL,
                                                address TEXT NOT NULL,
                                                latitude DECIMAL(10,7),
                                                longitude DECIMAL(10,7),
                                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                closed_date DATE,
                                                CONSTRAINT check_facility_type CHECK (
                                                    facility_type IN ('PVZ', 'WAREHOUSE', 'OFFICE')
                                                    )
);

CREATE TABLE shared_data.vehicles (
                                      vehicle_id BIGSERIAL PRIMARY KEY,
                                      license_plate VARCHAR(20) UNIQUE NOT NULL,
                                      capacity_kg DECIMAL(8,2),
                                      capacity_m3 DECIMAL(8,2),
                                      vehicle_status VARCHAR(20) DEFAULT 'ACTIVE',
                                      assigned_facility_id BIGINT REFERENCES shared_data.company_facilities(facility_id)
);

CREATE TABLE shared_data.pricing_rules (
                                           pricing_rule_id BIGSERIAL PRIMARY KEY,
                                           rule_name VARCHAR(100) NOT NULL,
                                           delivery_zone VARCHAR(50) NOT NULL DEFAULT 'CITY',
                                           weight_min DECIMAL(8,2),
                                           weight_max DECIMAL(8,2),
                                           base_price DECIMAL(10,2) NOT NULL,
                                           price_per_kg DECIMAL(10,2),
                                           effective_from DATE,
                                           effective_to DATE
);

CREATE TABLE shared_data.event_publication (
                                               id UUID NOT NULL PRIMARY KEY,
                                               listener_id VARCHAR(512) NOT NULL,
                                               event_type VARCHAR(512) NOT NULL,
                                               serialized_event TEXT NOT NULL,
                                               publication_date TIMESTAMP NOT NULL,
                                               completion_date TIMESTAMP
);

