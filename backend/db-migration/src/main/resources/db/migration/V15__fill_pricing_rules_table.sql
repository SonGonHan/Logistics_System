-- Заполнение справочника тарифов доставки
-- Тариф определяется зоной доставки и предельным весом.
-- Цена фиксированная (base_price). Выбирается тариф с наименьшим weight_max >= фактическому весу.

-- ============================================================================
-- ГОРОДСКАЯ ДОСТАВКА (CITY)
-- ============================================================================

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (1, 'Городская - до 1 кг', 'CITY', 1.00, 60, 40, 30, 150.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (2, 'Городская - до 5 кг', 'CITY', 5.00, 80, 60, 50, 350.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (3, 'Городская - до 10 кг', 'CITY', 10.00, 100, 80, 60, 650.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (4, 'Городская - до 20 кг', 'CITY', 20.00, 120, 90, 70, 1100.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (5, 'Городская - до 30 кг', 'CITY', 30.00, 150, 100, 80, 1800.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (6, 'Городская - до 50 кг', 'CITY', 50.00, 180, 120, 100, 2800.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- ============================================================================
-- ПРИГОРОДНАЯ ДОСТАВКА (SUBURBAN)
-- ============================================================================

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (7, 'Пригород - до 1 кг', 'SUBURBAN', 1.00, 60, 40, 30, 250.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (8, 'Пригород - до 5 кг', 'SUBURBAN', 5.00, 80, 60, 50, 550.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (9, 'Пригород - до 10 кг', 'SUBURBAN', 10.00, 100, 80, 60, 950.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (10, 'Пригород - до 20 кг', 'SUBURBAN', 20.00, 120, 90, 70, 1750.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (11, 'Пригород - до 30 кг', 'SUBURBAN', 30.00, 150, 100, 80, 2700.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (12, 'Пригород - до 50 кг', 'SUBURBAN', 50.00, 180, 120, 100, 4200.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- ============================================================================
-- МЕЖДУГОРОДНЯЯ ДОСТАВКА (INTERCITY)
-- ============================================================================

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (13, 'Межгород - до 1 кг', 'INTERCITY', 1.00, 60, 40, 30, 400.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (14, 'Межгород - до 5 кг', 'INTERCITY', 5.00, 80, 60, 50, 850.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (15, 'Межгород - до 10 кг', 'INTERCITY', 10.00, 100, 80, 60, 1500.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (16, 'Межгород - до 20 кг', 'INTERCITY', 20.00, 120, 90, 70, 2600.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (17, 'Межгород - до 30 кг', 'INTERCITY', 30.00, 150, 100, 80, 4000.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (18, 'Межгород - до 50 кг', 'INTERCITY', 50.00, 180, 120, 100, 6000.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

-- ============================================================================
-- РЕГИОНАЛЬНАЯ ДОСТАВКА (REGIONAL)
-- ============================================================================

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (19, 'Регион - до 1 кг', 'REGIONAL', 1.00, 60, 40, 30, 600.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (20, 'Регион - до 5 кг', 'REGIONAL', 5.00, 80, 60, 50, 1200.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (21, 'Регион - до 10 кг', 'REGIONAL', 10.00, 100, 80, 60, 2200.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (22, 'Регион - до 20 кг', 'REGIONAL', 20.00, 120, 90, 70, 3800.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (23, 'Регион - до 30 кг', 'REGIONAL', 30.00, 150, 100, 80, 6000.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

INSERT INTO shared_data.pricing_rules (pricing_rule_id, rule_name, delivery_zone, weight_max, max_length_cm, max_width_cm, max_height_cm, base_price, effective_from, effective_to) VALUES
    (24, 'Регион - до 50 кг', 'REGIONAL', 50.00, 180, 120, 100, 9000.00, '2025-01-01', NULL)
    ON CONFLICT (pricing_rule_id) DO NOTHING;

SELECT setval('shared_data.pricing_rules_pricing_rule_id_seq', (SELECT MAX(pricing_rule_id) FROM shared_data.pricing_rules));