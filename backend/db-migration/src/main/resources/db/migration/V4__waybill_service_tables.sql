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
                                                dimensions_declared VARCHAR(50),
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
                                          dimensions_actual VARCHAR(50),
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