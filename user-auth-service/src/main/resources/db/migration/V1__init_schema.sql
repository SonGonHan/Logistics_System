CREATE SCHEMA IF NOT EXISTS user_management;
CREATE SCHEMA IF NOT EXISTS waybill_service;
CREATE SCHEMA IF NOT EXISTS payment_service;
CREATE SCHEMA IF NOT EXISTS delivery_service;
CREATE SCHEMA IF NOT EXISTS warehouse_service;
CREATE SCHEMA IF NOT EXISTS hr_service;
CREATE SCHEMA IF NOT EXISTS rating_service;
CREATE SCHEMA IF NOT EXISTS shared_data;
CREATE SCHEMA IF NOT EXISTS archive;

-- ============================================================================
-- СХЕМА: user_management (User & Auth Service)
-- ============================================================================

-- Единая таблица пользователей (авторизованных и неавторизованных)
CREATE TABLE user_management.users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255),
    phone VARCHAR(20) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    role_name VARCHAR(50) NOT NULL DEFAULT 'UNREGISTERED_CONTACT',
    facility_id BIGINT,
    user_status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_accessed_at TIMESTAMP
);

-- Сессии пользователей
CREATE TABLE user_management.user_sessions (
    session_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50),
    user_agent TEXT,
    FOREIGN KEY (user_id) REFERENCES user_management.users(user_id) ON DELETE CASCADE
);

-- Журнал аудита
CREATE TABLE user_management.audit_logs (
    audit_log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    table_name VARCHAR(100),
    record_id BIGINT,
    new_values CLOB,
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50)
);

-- ============================================================================
-- СХЕМА: waybill_service (Waybill Service)
-- ============================================================================

-- Черновики накладных
CREATE TABLE waybill_service.waybill_drafts (
    draft_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    barcode VARCHAR(50) UNIQUE NOT NULL,
    draft_creator_id BIGINT NOT NULL,
    sender_user_id BIGINT NOT NULL,
    recipient_user_id BIGINT NOT NULL,
    recipient_address TEXT NOT NULL,
    weight_declared DECIMAL(8,2),
    dimensions_declared VARCHAR(50),
    estimated_price DECIMAL(10,2),
    draft_status VARCHAR(30) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Готовые накладные
CREATE TABLE waybill_service.waybills (
    waybill_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    waybill_number VARCHAR(50) UNIQUE NOT NULL,
    waybill_creator_id BIGINT NOT NULL,
    sender_user_id BIGINT NOT NULL,
    recipient_user_id BIGINT NOT NULL,
    recipient_address TEXT NOT NULL,
    weight_actual DECIMAL(8,2) NOT NULL,
    dimensions_actual VARCHAR(50),
    final_price DECIMAL(10,2) NOT NULL,
    waybill_status VARCHAR(50) DEFAULT 'ACCEPTED_AT_PVZ',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP
);

-- История статусов накладных
CREATE TABLE waybill_service.waybill_status_history (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    waybill_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    facility_id BIGINT,
    notes TEXT,
    changed_by BIGINT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (waybill_id) REFERENCES waybill_service.waybills(waybill_id) ON DELETE CASCADE
);

-- ============================================================================
-- СХЕМА: payment_service (Payment Service)
-- ============================================================================

-- Платежи
CREATE TABLE payment_service.payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    waybill_id BIGINT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    payment_method VARCHAR(20),
    transaction_id VARCHAR(100),
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    processed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Чеки
CREATE TABLE payment_service.receipts (
    receipt_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    receipt_number VARCHAR(50) NOT NULL,
    receipt_data CLOB,
    printed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payment_service.payments(payment_id) ON DELETE CASCADE
);

-- Возвраты
CREATE TABLE payment_service.refunds (
    refund_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT,
    amount DECIMAL(12,2) NOT NULL,
    reason VARCHAR(100) NOT NULL,
    refund_status VARCHAR(20) DEFAULT 'PENDING',
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payment_service.payments(payment_id)
);

-- ============================================================================
-- СХЕМА: delivery_service (Delivery Service)
-- ============================================================================

-- Маршруты курьеров
CREATE TABLE delivery_service.courier_routes (
    courier_route_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_name VARCHAR(100) NOT NULL,
    driver_id BIGINT,
    route_status VARCHAR(30) DEFAULT 'PLANNED',
    planned_start_time TIMESTAMP,
    actual_start_time TIMESTAMP,
    planned_end_time TIMESTAMP,
    actual_end_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Точки маршрута
CREATE TABLE delivery_service.route_points (
    route_point_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    courier_route_id BIGINT NOT NULL,
    point_order INTEGER NOT NULL,
    point_type VARCHAR(30) NOT NULL,
    custom_address TEXT,
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    point_status VARCHAR(30) DEFAULT 'PENDING',
    estimated_arrival TIMESTAMP,
    actual_arrival TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (courier_route_id) REFERENCES delivery_service.courier_routes(courier_route_id) ON DELETE CASCADE,
    UNIQUE(courier_route_id, point_order)
);

-- Связь точек маршрута с накладными
CREATE TABLE delivery_service.route_waybills (
    route_waybill_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_point_id BIGINT NOT NULL,
    waybill_id BIGINT NOT NULL,
    is_delivered BOOLEAN DEFAULT FALSE,
    delivered_at TIMESTAMP,
    FOREIGN KEY (route_point_id) REFERENCES delivery_service.route_points(route_point_id) ON DELETE CASCADE,
    UNIQUE(route_point_id, waybill_id)
);

-- ============================================================================
-- СХЕМА: warehouse_service (Warehouse Service)
-- ============================================================================

-- Складские запасы
CREATE TABLE warehouse_service.warehouse_inventory (
    inventory_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    waybill_id BIGINT,
    facility_id BIGINT NOT NULL,
    inventory_status VARCHAR(30) NOT NULL,
    arrived_at TIMESTAMP,
    departed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Грузовые перевозки
CREATE TABLE warehouse_service.cargo_shipments (
    cargo_shipment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shipment_number VARCHAR(50) UNIQUE NOT NULL,
    from_facility_id BIGINT NOT NULL,
    to_facility_id BIGINT NOT NULL,
    vehicle_id BIGINT,
    driver_id BIGINT,
    shipment_status VARCHAR(30) DEFAULT 'PLANNED',
    planned_departure TIMESTAMP,
    actual_departure TIMESTAMP,
    planned_arrival TIMESTAMP,
    actual_arrival TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Элементы грузовых перевозок
CREATE TABLE warehouse_service.cargo_shipment_items (
    shipment_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cargo_shipment_id BIGINT NOT NULL,
    waybill_id BIGINT NOT NULL,
    loaded_at TIMESTAMP,
    unloaded_at TIMESTAMP,
    FOREIGN KEY (cargo_shipment_id) REFERENCES warehouse_service.cargo_shipments(cargo_shipment_id) ON DELETE CASCADE
);

-- ============================================================================
-- СХЕМА: hr_service (HR Service)
-- ============================================================================

-- Справочник должностей
CREATE TABLE hr_service.positions (
    position_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    position_name VARCHAR(100) NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    region VARCHAR(100) NOT NULL,
    base_salary DECIMAL(12,2) NOT NULL,
    experience_bonus_percent DECIMAL(5,2) NOT NULL DEFAULT 5.00,
    max_experience_years INTEGER DEFAULT 10,
    UNIQUE(position_name, region)
);

-- Сотрудники
CREATE TABLE hr_service.employees (
    employee_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    employee_number VARCHAR(20) UNIQUE NOT NULL,
    position_id BIGINT,
    employment_status VARCHAR(30) DEFAULT 'ACTIVE',
    hire_date DATE NOT NULL,
    termination_date DATE,
    FOREIGN KEY (position_id) REFERENCES hr_service.positions(position_id)
);

-- Выплаты сотрудникам
CREATE TABLE hr_service.employee_payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT,
    payment_type VARCHAR(30) NOT NULL,
    period_start DATE,
    period_end DATE,
    amount DECIMAL(12,2) NOT NULL,
    description TEXT,
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES hr_service.employees(employee_id)
);

-- ============================================================================
-- СХЕМА: rating_service (Rating Service)
-- ============================================================================

-- Рейтинги и отзывы
CREATE TABLE rating_service.ratings_reviews (
    review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    waybill_id BIGINT,
    user_id BIGINT,
    rating INTEGER CHECK (rating BETWEEN 1 AND 5),
    review_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- СХЕМА: shared_data (Общие справочники)
-- ============================================================================

-- Служебные здания (ПВЗ, склады, офисы)
CREATE TABLE shared_data.company_facilities (
    facility_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    facility_type VARCHAR(20) NOT NULL,
    facility_name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_date DATE
);

-- Правила ценообразования
CREATE TABLE shared_data.pricing_rules (
    pricing_rule_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL,
    from_facility_type VARCHAR(20),
    to_facility_type VARCHAR(20),
    weight_min DECIMAL(8,2),
    weight_max DECIMAL(8,2),
    base_price DECIMAL(10,2) NOT NULL,
    price_per_kg DECIMAL(10,2),
    effective_from DATE,
    effective_to DATE
);

-- Транспортные средства
CREATE TABLE shared_data.vehicles (
    vehicle_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    capacity_kg DECIMAL(8,2),
    capacity_m3 DECIMAL(8,2),
    vehicle_status VARCHAR(20) DEFAULT 'ACTIVE',
    assigned_facility_id BIGINT,
    FOREIGN KEY (assigned_facility_id) REFERENCES shared_data.company_facilities(facility_id)
);

-- Системные настройки
CREATE TABLE shared_data.system_settings (
    setting_key VARCHAR(100) PRIMARY KEY,
    setting_value TEXT NOT NULL,
    description TEXT
);

-- ============================================================================
-- АРХИВНЫЕ ТАБЛИЦЫ (СХЕМА: archive)
-- ============================================================================

-- Архив пользователей
CREATE TABLE archive.users_archive (
    user_id BIGINT PRIMARY KEY,
    email VARCHAR(255),
    phone VARCHAR(20),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    role_name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Архив накладных
CREATE TABLE archive.waybills_archive (
    waybill_id BIGINT PRIMARY KEY,
    waybill_number VARCHAR(50) NOT NULL,
    waybill_creator_id BIGINT NOT NULL,
    sender_user_id BIGINT NOT NULL,
    recipient_user_id BIGINT NOT NULL,
    recipient_address TEXT NOT NULL,
    final_price DECIMAL(10,2) NOT NULL,
    waybill_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    accepted_at TIMESTAMP,
    archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Архив истории статусов
CREATE TABLE archive.waybill_status_history_archive (
    history_id BIGINT PRIMARY KEY,
    waybill_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    facility_id BIGINT,
    changed_by BIGINT,
    changed_at TIMESTAMP NOT NULL,
    archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Архив платежей
CREATE TABLE archive.payments_archive (
    payment_id BIGINT PRIMARY KEY,
    waybill_id BIGINT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP
);

-- Архив чеков
CREATE TABLE archive.receipts_archive (
    receipt_id BIGINT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    receipt_number VARCHAR(50) NOT NULL,
    printed_at TIMESTAMP,
    archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Архив возвратов
CREATE TABLE archive.refunds_archive (
    refund_id BIGINT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    refund_status VARCHAR(20) NOT NULL,
    requested_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP
);

-- Архив маршрутов
CREATE TABLE archive.courier_routes_archive (
    courier_route_id BIGINT PRIMARY KEY,
    route_name VARCHAR(100) NOT NULL,
    driver_id BIGINT NOT NULL,
    route_status VARCHAR(30) NOT NULL,
    actual_start_time TIMESTAMP,
    actual_end_time TIMESTAMP
);

-- Архив складских запасов
CREATE TABLE archive.warehouse_inventory_archive (
    inventory_id BIGINT PRIMARY KEY,
    waybill_id BIGINT,
    facility_id BIGINT NOT NULL,
    inventory_status VARCHAR(30) NOT NULL,
    arrived_at TIMESTAMP,
    departed_at TIMESTAMP,
    archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Архив грузовых перевозок
CREATE TABLE archive.cargo_shipments_archive (
    cargo_shipment_id BIGINT PRIMARY KEY,
    shipment_number VARCHAR(50) NOT NULL,
    from_facility_id BIGINT NOT NULL,
    to_facility_id BIGINT NOT NULL,
    driver_id BIGINT,
    vehicle_id BIGINT,
    shipment_status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    actual_departure TIMESTAMP,
    actual_arrival TIMESTAMP
);

-- Архив выплат сотрудников
CREATE TABLE archive.employee_payments_archive (
    payment_id BIGINT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    payment_type VARCHAR(30) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    period_start DATE,
    period_end DATE,
    paid_at TIMESTAMP
);

-- Архив сотрудников
CREATE TABLE archive.employees_archive (
    employee_id BIGINT PRIMARY KEY,
    user_id BIGINT,
    employee_number VARCHAR(20) NOT NULL,
    position_id BIGINT,
    hire_date DATE,
    termination_date DATE
);

-- Архив служебных зданий
CREATE TABLE archive.company_facilities_archive (
    facility_id BIGINT PRIMARY KEY,
    facility_type VARCHAR(20) NOT NULL,
    facility_name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    created_at TIMESTAMP,
    closed_date DATE
);

-- Архив уведомлений
CREATE TABLE archive.notification_queue_archive (
    notification_id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    channel_id BIGINT,
    notification_status VARCHAR(20) NOT NULL,
    sent_at TIMESTAMP,
    archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Архив аудита
CREATE TABLE archive.audit_logs_archive (
    audit_log_id BIGINT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    table_name VARCHAR(100),
    record_id BIGINT,
    new_values CLOB,
    performed_at TIMESTAMP,
    archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- ИНДЕКСЫ
-- ============================================================================

-- User Management
CREATE INDEX idx_users_email ON user_management.users(email);
CREATE INDEX idx_users_phone ON user_management.users(phone);
CREATE INDEX idx_users_role_name ON user_management.users(role_name);
CREATE INDEX idx_users_last_accessed ON user_management.users(last_accessed_at);
CREATE INDEX idx_user_sessions_user_id ON user_management.user_sessions(user_id);
CREATE INDEX idx_audit_logs_performed_at ON user_management.audit_logs(performed_at);
CREATE INDEX idx_audit_logs_user_id ON user_management.audit_logs(user_id);

-- Waybill Service
CREATE INDEX idx_waybill_drafts_creator ON waybill_service.waybill_drafts(draft_creator_id);
CREATE INDEX idx_waybill_drafts_sender ON waybill_service.waybill_drafts(sender_user_id);
CREATE INDEX idx_waybill_drafts_recipient ON waybill_service.waybill_drafts(recipient_user_id);
CREATE INDEX idx_waybills_creator ON waybill_service.waybills(waybill_creator_id);
CREATE INDEX idx_waybills_sender ON waybill_service.waybills(sender_user_id);
CREATE INDEX idx_waybills_recipient ON waybill_service.waybills(recipient_user_id);
CREATE INDEX idx_waybills_number ON waybill_service.waybills(waybill_number);
CREATE INDEX idx_waybills_status ON waybill_service.waybills(waybill_status);
CREATE INDEX idx_waybill_history_waybill_id ON waybill_service.waybill_status_history(waybill_id);

-- Payment Service
CREATE INDEX idx_payments_waybill_id ON payment_service.payments(waybill_id);
CREATE INDEX idx_receipts_payment_id ON payment_service.receipts(payment_id);

-- Delivery Service
CREATE INDEX idx_courier_routes_driver ON delivery_service.courier_routes(driver_id);
CREATE INDEX idx_route_points_route ON delivery_service.route_points(courier_route_id);
CREATE INDEX idx_route_waybills_point ON delivery_service.route_waybills(route_point_id);
CREATE INDEX idx_route_waybills_waybill ON delivery_service.route_waybills(waybill_id);

-- Warehouse Service
CREATE INDEX idx_warehouse_inventory_waybill ON warehouse_service.warehouse_inventory(waybill_id);
CREATE INDEX idx_warehouse_inventory_facility ON warehouse_service.warehouse_inventory(facility_id);
CREATE INDEX idx_cargo_shipments_from_facility ON warehouse_service.cargo_shipments(from_facility_id);
CREATE INDEX idx_cargo_shipments_to_facility ON warehouse_service.cargo_shipments(to_facility_id);

-- HR Service
CREATE INDEX idx_employees_user_id ON hr_service.employees(user_id);
CREATE INDEX idx_employees_position_id ON hr_service.employees(position_id);
CREATE INDEX idx_employee_payments_employee ON hr_service.employee_payments(employee_id);

-- Shared Data
CREATE INDEX idx_company_facilities_type ON shared_data.company_facilities(facility_type);
CREATE INDEX idx_company_facilities_closed_date ON shared_data.company_facilities(closed_date);

-- ============================================================================
-- КОНЕЦ СХЕМЫ
-- ============================================================================
-- ✅ Готово! Схема совместима с PostgreSQL и H2
-- ✅ Все таблицы, архивы и индексы созданы
-- ✅ Используйте с Flyway V1__init_schema.sql
