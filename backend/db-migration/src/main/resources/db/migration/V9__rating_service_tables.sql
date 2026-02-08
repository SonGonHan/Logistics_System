-- ============================================================================
-- 9. СОЗДАНИЕ ТАБЛИЦ rating_service
-- ============================================================================

CREATE TABLE rating_service.ratings_reviews (
                                                review_id BIGSERIAL PRIMARY KEY,
                                                waybill_id BIGINT REFERENCES waybill_service.waybills(waybill_id) ON DELETE SET NULL,
                                                user_id BIGINT REFERENCES user_management.users(user_id) ON DELETE SET NULL,
                                                rating INTEGER CHECK (rating BETWEEN 1 AND 5),
                                                review_text TEXT,
                                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);