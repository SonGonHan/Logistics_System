-- ============================================================================
-- 7. СОЗДАНИЕ ТАБЛИЦ warehouse_service
-- ============================================================================

CREATE TABLE warehouse_service.warehouse_inventory (
                                                       inventory_id BIGSERIAL PRIMARY KEY,
                                                       waybill_id BIGINT REFERENCES waybill_service.waybills(waybill_id) ON DELETE SET NULL,
                                                       facility_id BIGINT NOT NULL REFERENCES shared_data.company_facilities(facility_id) ON DELETE RESTRICT,
                                                       inventory_status VARCHAR(30) NOT NULL,
                                                       arrived_at TIMESTAMP,
                                                       departed_at TIMESTAMP,
                                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN warehouse_service.warehouse_inventory.arrived_at IS 'Время прибытия на склад';

CREATE TABLE warehouse_service.cargo_shipments (
                                                   cargo_shipment_id BIGSERIAL PRIMARY KEY,
                                                   shipment_number VARCHAR(50) UNIQUE NOT NULL,
                                                   from_facility_id BIGINT NOT NULL REFERENCES shared_data.company_facilities(facility_id) ON DELETE RESTRICT,
                                                   to_facility_id BIGINT NOT NULL REFERENCES shared_data.company_facilities(facility_id) ON DELETE RESTRICT,
                                                   vehicle_id BIGINT REFERENCES shared_data.vehicles(vehicle_id) ON DELETE SET NULL,
                                                   driver_id BIGINT REFERENCES user_management.users(user_id) ON DELETE SET NULL,
                                                   shipment_status VARCHAR(30) DEFAULT 'PLANNED',
                                                   planned_departure TIMESTAMP,
                                                   actual_departure TIMESTAMP,
                                                   planned_arrival TIMESTAMP,
                                                   actual_arrival TIMESTAMP,
                                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN warehouse_service.cargo_shipments.from_facility_id IS 'Может быть склад или ПВЗ';
COMMENT ON COLUMN warehouse_service.cargo_shipments.to_facility_id IS 'Может быть склад или ПВЗ';

CREATE TABLE warehouse_service.cargo_shipment_items (
                                                        shipment_item_id BIGSERIAL PRIMARY KEY,
                                                        cargo_shipment_id BIGINT NOT NULL REFERENCES warehouse_service.cargo_shipments(cargo_shipment_id) ON DELETE CASCADE,
                                                        waybill_id BIGINT NOT NULL REFERENCES waybill_service.waybills(waybill_id) ON DELETE CASCADE,
                                                        loaded_at TIMESTAMP,
                                                        unloaded_at TIMESTAMP
);