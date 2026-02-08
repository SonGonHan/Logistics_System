-- ============================================================================
-- 5. СОЗДАНИЕ ТАБЛИЦ payment_service
-- ============================================================================

CREATE TABLE payment_service.payments (
                                          payment_id BIGSERIAL PRIMARY KEY,
                                          waybill_id BIGINT NOT NULL REFERENCES waybill_service.waybills(waybill_id) ON DELETE RESTRICT,
                                          amount DECIMAL(12,2) NOT NULL,
                                          payment_method VARCHAR(20),
                                          transaction_id VARCHAR(100),
                                          payment_status VARCHAR(20) DEFAULT 'PENDING',
                                          processed_at TIMESTAMP,
                                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          CONSTRAINT check_payment_method CHECK (
                                              payment_method IN ('CASH', 'CARD_TERMINAL', 'SBP')
                                              )
);

COMMENT ON CONSTRAINT check_payment_method ON payment_service.payments IS 'Поддерживаемые методы оплаты';

CREATE TABLE payment_service.receipts (
                                          receipt_id BIGSERIAL PRIMARY KEY,
                                          payment_id BIGINT NOT NULL REFERENCES payment_service.payments(payment_id) ON DELETE CASCADE,
                                          receipt_number VARCHAR(50) NOT NULL,
                                          receipt_data JSONB,
                                          printed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payment_service.refunds (
                                         refund_id BIGSERIAL PRIMARY KEY,
                                         payment_id BIGINT NOT NULL REFERENCES payment_service.payments(payment_id) ON DELETE CASCADE,
                                         amount DECIMAL(12,2) NOT NULL,
                                         reason VARCHAR(100) NOT NULL,
                                         refund_status VARCHAR(20) DEFAULT 'PENDING',
                                         requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                         approved_at TIMESTAMP,
                                         completed_at TIMESTAMP,
                                         CONSTRAINT check_refund_reason CHECK (
                                             reason IN ('DAMAGED', 'WRONG_ADDRESS', 'CANCELLED_BY_CLIENT', 'LOST', 'OTHER')
                                             )
);