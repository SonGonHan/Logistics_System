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
                                                 actual_arrival TIMESTAMP,
                                                 archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Архив выплат сотрудников
CREATE TABLE archive.employee_payments_archive (
                                                   payment_id BIGINT PRIMARY KEY,
                                                   employee_id BIGINT NOT NULL,
                                                   payment_type VARCHAR(30) NOT NULL,
                                                   amount DECIMAL(12,2) NOT NULL,
                                                   period_start DATE,
                                                   period_end DATE,
                                                   paid_at TIMESTAMP,
                                                   archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Архив сотрудников
CREATE TABLE archive.employees_archive (
                                           employee_id BIGINT PRIMARY KEY,
                                           user_id BIGINT,
                                           employee_number VARCHAR(20) NOT NULL,
                                           position_id BIGINT,
                                           hire_date DATE,
                                           termination_date DATE,
                                           archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Архив служебных зданий
CREATE TABLE archive.company_facilities_archive (
                                                    facility_id BIGINT PRIMARY KEY,
                                                    facility_type VARCHAR(20) NOT NULL,
                                                    facility_name VARCHAR(255) NOT NULL,
                                                    address TEXT NOT NULL,
                                                    created_at TIMESTAMP,
                                                    closed_date DATE,
                                                    archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Архив аудита
CREATE TABLE archive.audit_logs_archive (
                                            audit_log_id BIGINT PRIMARY KEY,
                                            user_id BIGINT,
                                            action_type_id SMALLINT NOT NULL,
                                            table_name VARCHAR(100),
                                            record_id BIGINT,
                                            new_values JSONB,
                                            performed_at TIMESTAMP,
                                            archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);