INSERT INTO user_management.users (user_id, email, phone, password_hash, first_name, last_name, middle_name, role_name, facility_id, user_status)
VALUES (-1, NULL, '9999999999', NULL, 'System', 'Audit', NULL, 'SYSTEM', NULL, 'ACTIVE')
ON CONFLICT (user_id) DO NOTHING;