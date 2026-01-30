-- V4__fix_audit_action_types_sequence.sql
-- Исправление sequence после вставки записей с явными ID в V3

-- Обновление sequence на максимальный ID + 1
SELECT setval('shared_data.audit_action_types_action_type_id_seq', (SELECT COALESCE(MAX(action_type_id), 0) + 1 FROM shared_data.audit_action_types), false);