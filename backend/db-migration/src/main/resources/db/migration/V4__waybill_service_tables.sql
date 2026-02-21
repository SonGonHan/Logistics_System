-- ============================================================================
-- 4. СОЗДАНИЕ ТАБЛИЦ waybill_service
-- ============================================================================

CREATE TABLE waybill_service.waybill_drafts (
                                                draft_id BIGSERIAL PRIMARY KEY,
                                                barcode VARCHAR(50) UNIQUE NOT NULL,
                                                draft_creator_id BIGINT NOT NULL REFERENCES user_management.users(user_id) ON DELETE RESTRICT,
                                                sender_user_id BIGINT NOT NULL REFERENCES user_management.users(user_id) ON DELETE RESTRICT,
                                                recipient_user_id BIGINT NOT NULL REFERENCES user_management.users(user_id) ON DELETE RESTRICT,
                                                recipient_address TEXT NOT NULL,
                                                weight_declared DECIMAL(8,2),
                                                length_declared_cm DECIMAL(8,2),
                                                width_declared_cm DECIMAL(8,2),
                                                height_declared_cm DECIMAL(8,2),
                                                pricing_rule_id BIGINT REFERENCES shared_data.pricing_rules(pricing_rule_id),
                                                estimated_price DECIMAL(10,2),
                                                draft_status VARCHAR(30) DEFAULT 'PENDING',
                                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                CONSTRAINT check_draft_status CHECK (
                                                    draft_status IN ('PENDING', 'CONFIRMED', 'CANCELLED')
                                                    )
);

COMMENT ON TABLE waybill_service.waybill_drafts IS 'Черновики накладных со ссылками на users';
COMMENT ON COLUMN waybill_service.waybill_drafts.sender_user_id IS 'ID отправителя';
COMMENT ON COLUMN waybill_service.waybill_drafts.recipient_user_id IS 'ID получателя';

CREATE TABLE waybill_service.waybills (
                                          waybill_id BIGSERIAL PRIMARY KEY,
                                          waybill_number VARCHAR(50) UNIQUE NOT NULL,
                                          waybill_creator_id BIGINT NOT NULL REFERENCES user_management.users(user_id) ON DELETE RESTRICT,
                                          sender_user_id BIGINT NOT NULL REFERENCES user_management.users(user_id) ON DELETE RESTRICT,
                                          recipient_user_id BIGINT NOT NULL REFERENCES user_management.users(user_id) ON DELETE RESTRICT,
                                          recipient_address TEXT NOT NULL,
                                          weight_actual DECIMAL(8,2) NOT NULL,
                                          length_cm DECIMAL(8,2),
                                          width_cm DECIMAL(8,2),
                                          height_cm DECIMAL(8,2),
                                          pricing_rule_id BIGINT REFERENCES shared_data.pricing_rules(pricing_rule_id),
                                          final_price DECIMAL(10,2) NOT NULL,
                                          waybill_status VARCHAR(50) DEFAULT 'ACCEPTED_AT_PVZ',
                                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          accepted_at TIMESTAMP
);

COMMENT ON COLUMN waybill_service.waybills.waybill_creator_id IS 'Оператор ПВЗ, который подтвердил и создал накладную';
COMMENT ON COLUMN waybill_service.waybills.sender_user_id IS 'ID отправителя (ссылка на users)';
COMMENT ON COLUMN waybill_service.waybills.recipient_user_id IS 'ID получателя (ссылка на users)';

CREATE TABLE waybill_service.waybill_status_history (
                                                        history_id BIGSERIAL PRIMARY KEY,
                                                        waybill_id BIGINT NOT NULL REFERENCES waybill_service.waybills(waybill_id) ON DELETE CASCADE,
                                                        status VARCHAR(50) NOT NULL,
                                                        facility_id BIGINT,
                                                        notes TEXT,
                                                        changed_by BIGINT REFERENCES user_management.users(user_id) ON DELETE SET NULL,
                                                        changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Constraints for dimensions in waybill_drafts
ALTER TABLE waybill_service.waybill_drafts
    ADD CONSTRAINT chk_draft_length_positive CHECK (length_declared_cm IS NULL OR length_declared_cm > 0),
    ADD CONSTRAINT chk_draft_width_positive CHECK (width_declared_cm IS NULL OR width_declared_cm > 0),
    ADD CONSTRAINT chk_draft_height_positive CHECK (height_declared_cm IS NULL OR height_declared_cm > 0),
    ADD CONSTRAINT chk_draft_dimensions_complete CHECK (
        (length_declared_cm IS NULL AND width_declared_cm IS NULL AND height_declared_cm IS NULL) OR
        (length_declared_cm IS NOT NULL AND width_declared_cm IS NOT NULL AND height_declared_cm IS NOT NULL)
    );

-- Constraints for dimensions in waybills
ALTER TABLE waybill_service.waybills
    ADD CONSTRAINT chk_length_positive CHECK (length_cm IS NULL OR length_cm > 0),
    ADD CONSTRAINT chk_width_positive CHECK (width_cm IS NULL OR width_cm > 0),
    ADD CONSTRAINT chk_height_positive CHECK (height_cm IS NULL OR height_cm > 0),
    ADD CONSTRAINT chk_dimensions_complete CHECK (
        (length_cm IS NULL AND width_cm IS NULL AND height_cm IS NULL) OR
        (length_cm IS NOT NULL AND width_cm IS NOT NULL AND height_cm IS NOT NULL)
    );

COMMENT ON COLUMN waybill_service.waybill_drafts.length_declared_cm IS 'Заявленная длина посылки в сантиметрах';
COMMENT ON COLUMN waybill_service.waybill_drafts.width_declared_cm IS 'Заявленная ширина посылки в сантиметрах';
COMMENT ON COLUMN waybill_service.waybill_drafts.height_declared_cm IS 'Заявленная высота посылки в сантиметрах';

COMMENT ON COLUMN waybill_service.waybills.length_cm IS 'Фактическая длина посылки в сантиметрах';
COMMENT ON COLUMN waybill_service.waybills.width_cm IS 'Фактическая ширина посылки в сантиметрах';
COMMENT ON COLUMN waybill_service.waybills.height_cm IS 'Фактическая высота посылки в сантиметрах';

-- Constraint for waybill_status
ALTER TABLE waybill_service.waybills
    ADD CONSTRAINT chk_waybill_status CHECK (
        waybill_status IN (
            'ACCEPTED_AT_PVZ',
            'IN_TRANSIT',
            'AT_SORTING_CENTER',
            'OUT_FOR_DELIVERY',
            'READY_FOR_PICKUP',
            'DELIVERED',
            'RETURNING',
            'RETURNED',
            'CANCELLED',
            'LOST'
        )
    );

-- Constraint for waybill_status_history.status
ALTER TABLE waybill_service.waybill_status_history
    ADD CONSTRAINT chk_status_history_status CHECK (
        status IN (
            'ACCEPTED_AT_PVZ',
            'IN_TRANSIT',
            'AT_SORTING_CENTER',
            'OUT_FOR_DELIVERY',
            'READY_FOR_PICKUP',
            'DELIVERED',
            'RETURNING',
            'RETURNED',
            'CANCELLED',
            'LOST'
        )
    );