-- ============================================================================
-- 6. СОЗДАНИЕ ТАБЛИЦ delivery_service
-- ============================================================================

CREATE TABLE delivery_service.courier_routes (
                                                 courier_route_id BIGSERIAL PRIMARY KEY,
                                                 route_name VARCHAR(100) NOT NULL,
                                                 driver_id BIGINT REFERENCES user_management.users(user_id) ON DELETE SET NULL,
                                                 route_status VARCHAR(30) DEFAULT 'PLANNED',
                                                 planned_start_time TIMESTAMP,
                                                 actual_start_time TIMESTAMP,
                                                 planned_end_time TIMESTAMP,
                                                 actual_end_time TIMESTAMP,
                                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE delivery_service.route_points (
                                               route_point_id BIGSERIAL PRIMARY KEY,
                                               courier_route_id BIGINT NOT NULL REFERENCES delivery_service.courier_routes(courier_route_id) ON DELETE CASCADE,
                                               point_order INTEGER NOT NULL,
                                               point_type VARCHAR(30) NOT NULL,
                                               custom_address TEXT,
                                               latitude DECIMAL(10,7),
                                               longitude DECIMAL(10,7),
                                               point_status VARCHAR(30) DEFAULT 'PENDING',
                                               estimated_arrival TIMESTAMP,
                                               actual_arrival TIMESTAMP,
                                               notes TEXT,
                                               CONSTRAINT unique_point_order UNIQUE(courier_route_id, point_order),
                                               CONSTRAINT check_point_type CHECK (point_type IN ('PICKUP', 'DELIVERY', 'WAREHOUSE')),
                                               CONSTRAINT check_point_status CHECK (
                                                   point_status IN ('PENDING', 'IN_PROGRESS', 'PARTIALLY_COMPLETED', 'COMPLETED', 'SKIPPED')
                                                   )
);

COMMENT ON COLUMN delivery_service.route_points.point_status IS 'PARTIALLY_COMPLETED - доставлена только часть посылок';
COMMENT ON COLUMN delivery_service.route_points.custom_address IS 'Только для служебных точек (склады, ПВЗ)';

CREATE TABLE delivery_service.route_waybills (
                                                 route_waybill_id BIGSERIAL PRIMARY KEY,
                                                 route_point_id BIGINT NOT NULL REFERENCES delivery_service.route_points(route_point_id) ON DELETE CASCADE,
                                                 waybill_id BIGINT NOT NULL REFERENCES waybill_service.waybills(waybill_id) ON DELETE CASCADE,
                                                 is_delivered BOOLEAN DEFAULT FALSE,
                                                 delivered_at TIMESTAMP,
                                                 UNIQUE(route_point_id, waybill_id)
);
