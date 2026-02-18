-- Заполнение справочника объектов компании (ПВЗ, склады, офисы)
-- 3 города: Вологда, Череповец, Ярославль
-- В каждом городе: 3 ПВЗ, 1 склад, 1 офис

-- ============================================================================
-- ВОЛОГДА
-- ============================================================================

-- ПВЗ
INSERT INTO shared_data.company_facilities (facility_id, facility_type, facility_name, address, latitude, longitude, created_at)
    VALUES (1, 'PVZ', 'ПВЗ Вологда-1', 'г. Вологда, ул. Мира, д. 15', 59.2231400, 39.8797600, '2025-01-10 09:00:00')
    ON CONFLICT (facility_id) DO NOTHING;

INSERT INTO shared_data.company_facilities (facility_id, facility_type, facility_name, address, latitude, longitude, created_at)
    VALUES (2, 'PVZ', 'ПВЗ Вологда-2', 'г. Вологда, ул. Ленинградская, д. 71', 59.2168300, 39.8663100, '2025-01-10 09:00:00')
    ON CONFLICT (facility_id) DO NOTHING;

INSERT INTO shared_data.company_facilities (facility_id, facility_type, facility_name, address, latitude, longitude, created_at)
    VALUES (3, 'PVZ', 'ПВЗ Вологда-3', 'г. Вологда, ул. Пошехонское шоссе, д. 22', 59.2314700, 39.9012500, '2025-01-15 09:00:00')
    ON CONFLICT (facility_id) DO NOTHING;

-- Склад
INSERT INTO shared_data.company_facilities (facility_id, facility_type, facility_name, address, latitude, longitude, created_at)
    VALUES (4, 'WAREHOUSE', 'Склад Вологда', 'г. Вологда, ул. Кирова, д. 5', 59.2093800, 39.8591200, '2025-01-05 08:00:00')
    ON CONFLICT (facility_id) DO NOTHING;

-- Офис
INSERT INTO shared_data.company_facilities (facility_id, facility_type, facility_name, address, latitude, longitude, created_at)
    VALUES (5, 'OFFICE', 'Офис Вологда', 'г. Вологда, Советский пр., д. 93', 59.2239000, 39.8845000, '2025-01-05 08:00:00')
    ON CONFLICT (facility_id) DO NOTHING;

-- ============================================================================
-- ЧЕРЕПОВЕЦ
-- ============================================================================

-- ПВЗ
INSERT INTO shared_data.company_facilities (facility_id, facility_type, facility_name, address, latitude, longitude, created_at)
    VALUES (6, 'PVZ', 'ПВЗ Череповец-1', 'г. Череповец, Советский пр., д. 57', 59.1282000, 37.9028000, '2025-01-10 09:00:00')
    ON CONFLICT (facility_id) DO NOTHING;

INSERT INTO shared_data.company_facilities (facility_id, facility_type, facility_name, address, latitude, longitude, created_at)
    VALUES (7, 'PVZ', 'ПВЗ Череповец-2', 'г. Череповец, ул. Ленина, д. 83', 59.1197400, 37.8893500, '2025-01-10 09:00:00')
    ON CONFLICT (facility_id) DO NOTHING;

INSERT INTO shared_data.company_facilities (facility_id, facility_type, facility_name, address, latitude, longitude, created_at)
    VALUES (8, 'PVZ', 'ПВЗ Череповец-3', 'г. Череповец, ул. Командарма Белова, д. 39', 59.1358200, 37.9213700, '2025-01-15 09:00:00')
    ON CONFLICT (facility_id) DO NOTHING;

-- Склад
INSERT INTO shared_data.company_facilities (facility_id, facility_type, facility_name, address, latitude, longitude, created_at)
    VALUES (9, 'WAREHOUSE', 'Склад Череповец', 'г. Череповец, ул. Архангельская, д. 12', 59.1089600, 37.8761400, '2025-01-05 08:00:00')
    ON CONFLICT (facility_id) DO NOTHING;

-- Офис
INSERT INTO shared_data.company_facilities (facility_id, facility_type, facility_name, address, latitude, longitude, created_at)
    VALUES (10, 'OFFICE', 'Офис Череповец', 'г. Череповец, Советский пр., д. 102', 59.1275100, 37.9071300, '2025-01-05 08:00:00')
    ON CONFLICT (facility_id) DO NOTHING;

-- ============================================================================
-- ЯРОСЛАВЛЬ
-- ============================================================================

-- ПВЗ
INSERT INTO shared_data.company_facilities (facility_id, facility_type, facility_name, address, latitude, longitude, created_at)
    VALUES (11, 'PVZ', 'ПВЗ Ярославль-1', 'г. Ярославль, ул. Победы, д. 10', 57.6261000, 39.8845000, '2025-01-10 09:00:00')
    ON CONFLICT (facility_id) DO NOTHING;

INSERT INTO shared_data.company_facilities (facility_id, facility_type, facility_name, address, latitude, longitude, created_at)
    VALUES (12, 'PVZ', 'ПВЗ Ярославль-2', 'г. Ярославль, ул. Свободы, д. 62', 57.6204800, 39.8714300, '2025-01-10 09:00:00')
    ON CONFLICT (facility_id) DO NOTHING;

INSERT INTO shared_data.company_facilities (facility_id, facility_type, facility_name, address, latitude, longitude, created_at)
    VALUES (13, 'PVZ', 'ПВЗ Ярославль-3', 'г. Ярославль, ул. Щапова, д. 26', 57.6318500, 39.9036200, '2025-01-15 09:00:00')
    ON CONFLICT (facility_id) DO NOTHING;

-- Склад
INSERT INTO shared_data.company_facilities (facility_id, facility_type, facility_name, address, latitude, longitude, created_at)
    VALUES (14, 'WAREHOUSE', 'Склад Ярославль', 'г. Ярославль, ул. Промышленная, д. 3', 57.6094200, 39.8572800, '2025-01-05 08:00:00')
    ON CONFLICT (facility_id) DO NOTHING;

-- Офис
INSERT INTO shared_data.company_facilities (facility_id, facility_type, facility_name, address, latitude, longitude, created_at)
    VALUES (15, 'OFFICE', 'Офис Ярославль', 'г. Ярославль, ул. Депутатская, д. 21', 57.6257300, 39.8812600, '2025-01-05 08:00:00')
    ON CONFLICT (facility_id) DO NOTHING;

SELECT setval('shared_data.company_facilities_facility_id_seq', (SELECT MAX(facility_id) FROM shared_data.company_facilities));