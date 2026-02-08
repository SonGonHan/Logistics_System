-- Заполнение справочника тарифов доставки

-- ============================================================================
-- ГОРОДСКАЯ ДОСТАВКА (CITY)
-- ============================================================================

-- Легкие посылки (до 1 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (1, 'Городская - до 1 кг', 'CITY', 0.00, 1.00, 150.00, 0.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Малый вес (1-5 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (2, 'Городская - 1-5 кг', 'CITY', 1.01, 5.00, 200.00, 30.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Средний вес (5-10 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (3, 'Городская - 5-10 кг', 'CITY', 5.01, 10.00, 300.00, 50.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Большой вес (10-20 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (4, 'Городская - 10-20 кг', 'CITY', 10.01, 20.00, 500.00, 70.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Тяжелые грузы (20-30 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (5, 'Городская - 20-30 кг', 'CITY', 20.01, 30.00, 800.00, 90.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Очень тяжелые грузы (свыше 30 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (6, 'Городская - свыше 30 кг', 'CITY', 30.01, NULL, 1200.00, 110.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- ============================================================================
-- ПРИГОРОДНАЯ ДОСТАВКА (SUBURBAN)
-- ============================================================================

-- Легкие посылки (до 1 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (7, 'Пригород - до 1 кг', 'SUBURBAN', 0.00, 1.00, 250.00, 0.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Малый вес (1-5 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (8, 'Пригород - 1-5 кг', 'SUBURBAN', 1.01, 5.00, 350.00, 50.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Средний вес (5-10 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (9, 'Пригород - 5-10 кг', 'SUBURBAN', 5.01, 10.00, 500.00, 75.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Большой вес (10-20 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (10, 'Пригород - 10-20 кг', 'SUBURBAN', 10.01, 20.00, 750.00, 100.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Тяжелые грузы (20-30 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (11, 'Пригород - 20-30 кг', 'SUBURBAN', 20.01, 30.00, 1200.00, 130.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Очень тяжелые грузы (свыше 30 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (12, 'Пригород - свыше 30 кг', 'SUBURBAN', 30.01, NULL, 1800.00, 150.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- ============================================================================
-- МЕЖДУГОРОДНЯЯ ДОСТАВКА (INTERCITY)
-- ============================================================================

-- Легкие посылки (до 1 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (13, 'Межгород - до 1 кг', 'INTERCITY', 0.00, 1.00, 400.00, 0.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Малый вес (1-5 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (14, 'Межгород - 1-5 кг', 'INTERCITY', 1.01, 5.00, 600.00, 80.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Средний вес (5-10 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (15, 'Межгород - 5-10 кг', 'INTERCITY', 5.01, 10.00, 900.00, 120.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Большой вес (10-20 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (16, 'Межгород - 10-20 кг', 'INTERCITY', 10.01, 20.00, 1400.00, 150.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Тяжелые грузы (20-30 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (17, 'Межгород - 20-30 кг', 'INTERCITY', 20.01, 30.00, 2000.00, 180.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Очень тяжелые грузы (свыше 30 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (18, 'Межгород - свыше 30 кг', 'INTERCITY', 30.01, NULL, 3000.00, 200.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- ============================================================================
-- РЕГИОНАЛЬНАЯ ДОСТАВКА (REGIONAL)
-- ============================================================================

-- Легкие посылки (до 1 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (19, 'Регион - до 1 кг', 'REGIONAL', 0.00, 1.00, 600.00, 0.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Малый вес (1-5 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (20, 'Регион - 1-5 кг', 'REGIONAL', 1.01, 5.00, 900.00, 120.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Средний вес (5-10 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (21, 'Регион - 5-10 кг', 'REGIONAL', 5.01, 10.00, 1400.00, 180.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Большой вес (10-20 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (22, 'Регион - 10-20 кг', 'REGIONAL', 10.01, 20.00, 2200.00, 220.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Тяжелые грузы (20-30 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (23, 'Регион - 20-30 кг', 'REGIONAL', 20.01, 30.00, 3200.00, 260.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- Очень тяжелые грузы (свыше 30 кг)
INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_min, weight_max, base_price, price_per_kg, effective_from, effective_to) VALUES
    (24, 'Регион - свыше 30 кг', 'REGIONAL', 30.01, NULL, 4500.00, 300.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

SELECT setval('shared_data.pricing_rules_pricing_rule_id_seq', (SELECT MAX(pricing_rule_id) FROM shared_data.pricing_rules));
