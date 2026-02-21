-- ============================================================================
-- ИНДЕКСЫ
-- ============================================================================

-- User Management
CREATE INDEX idx_users_email ON user_management.users(email);
CREATE INDEX idx_users_phone ON user_management.users(phone);
CREATE INDEX idx_users_role_name ON user_management.users(role_name);
CREATE INDEX idx_users_last_accessed ON user_management.users(last_accessed_at);
CREATE INDEX idx_user_sessions_user_id ON user_management.user_sessions(user_id);
CREATE INDEX idx_audit_logs_user_id ON user_management.audit_logs(user_id);
CREATE INDEX idx_audit_logs_action_type_id ON user_management.audit_logs(action_type_id);
CREATE INDEX idx_audit_logs_record ON user_management.audit_logs(table_name, record_id);
CREATE INDEX idx_audit_logs_performed_at ON user_management.audit_logs(performed_at);

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
CREATE INDEX idx_event_publication_by_completion_date ON shared_data.event_publication(completion_date);
