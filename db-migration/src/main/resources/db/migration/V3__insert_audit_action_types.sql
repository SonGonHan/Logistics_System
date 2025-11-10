-- V3__insert_audit_action_types.sql
-- Заполнение справочника типов действий для журнала аудита

-- Категория: Authentication
INSERT INTO shared_data.audit_action_types (action_type_id, action_name, category, description) VALUES
                                                                                                    (1, 'USER_REGISTER', 'Authentication', 'Регистрация нового пользователя'),
                                                                                                    (2, 'USER_LOGIN_SUCCESS', 'Authentication', 'Успешный вход в систему'),
                                                                                                    (3, 'USER_LOGIN_FAILURE', 'Authentication', 'Неудачная попытка входа'),
                                                                                                    (4, 'USER_LOGOUT', 'Authentication', 'Выход из системы'),
                                                                                                    (5, 'PASSWORD_CHANGE', 'Authentication', 'Смена пароля'),
                                                                                                    (6, 'PASSWORD_RESET_REQUEST', 'Authentication', 'Запрос сброса пароля'),
                                                                                                    (7, 'SESSION_CREATE', 'Authentication', 'Создание новой сессии'),
                                                                                                    (8, 'SESSION_REVOKE', 'Authentication', 'Отзыв сессии'),
                                                                                                    (9, 'TOKEN_REFRESH', 'Authentication', 'Обновление токена доступа')
    ON CONFLICT (action_type_id) DO NOTHING;

-- Категория: User Management
INSERT INTO shared_data.audit_action_types (action_type_id, action_name, category, description) VALUES
                                                                                                    (10, 'USER_CREATE', 'User Management', 'Создание пользователя администратором/HR'),
                                                                                                    (11, 'USER_UPDATE', 'User Management', 'Изменение данных пользователя'),
                                                                                                    (12, 'USER_ROLE_CHANGE', 'User Management', 'Изменение роли пользователя'),
                                                                                                    (13, 'USER_STATUS_CHANGE', 'User Management', 'Изменение статуса пользователя (активный/заблокирован)'),
                                                                                                    (14, 'USER_UPGRADE_TO_CLIENT', 'User Management', 'Переход неавторизованного контакта в статус клиента'),
                                                                                                    (15, 'USER_DELETE', 'User Management', 'Удаление/деактивация пользователя'),
                                                                                                    (16, 'USER_FACILITY_CHANGE', 'User Management', 'Изменение привязки пользователя к объекту (ПВЗ/склад)')
    ON CONFLICT (action_type_id) DO NOTHING;

-- Категория: Waybills
INSERT INTO shared_data.audit_action_types (action_type_id, action_name, category, description) VALUES
                                                                                                    (17, 'DRAFT_CREATE', 'Waybills', 'Создание черновика накладной'),
                                                                                                    (18, 'DRAFT_UPDATE', 'Waybills', 'Редактирование черновика'),
                                                                                                    (19, 'DRAFT_CANCEL', 'Waybills', 'Отмена черновика'),
                                                                                                    (20, 'WAYBILL_FINALIZE', 'Waybills', 'Конвертация черновика в накладную'),
                                                                                                    (21, 'WAYBILL_STATUS_CHANGE', 'Waybills', 'Изменение статуса накладной'),
                                                                                                    (22, 'WAYBILL_PAYMENT_MODE_CHANGE', 'Waybills', 'Изменение плательщика/метода оплаты'),
                                                                                                    (23, 'WAYBILL_CONFIRM', 'Waybills', 'Подтверждение условий накладной'),
                                                                                                    (24, 'WAYBILL_CANCEL', 'Waybills', 'Отмена накладной'),
                                                                                                    (25, 'WAYBILL_REROUTE', 'Waybills', 'Переадресация накладной'),
                                                                                                    (26, 'WAYBILL_SERVICE_ADD', 'Waybills', 'Добавление дополнительной услуги к накладной')
    ON CONFLICT (action_type_id) DO NOTHING;

-- Категория: Pvz Operations
INSERT INTO shared_data.audit_action_types (action_type_id, action_name, category, description) VALUES
                                                                                                    (27, 'PVZ_ACCEPTANCE_START', 'Pvz Operations', 'Начало приемки на ПВЗ'),
                                                                                                    (28, 'PVZ_WEIGHT_VERIFY', 'Pvz Operations', 'Проверка веса/габаритов на ПВЗ'),
                                                                                                    (29, 'PVZ_PHOTO_UPLOAD', 'Pvz Operations', 'Загрузка фото посылки на ПВЗ'),
                                                                                                    (30, 'PVZ_LABEL_PRINT', 'Pvz Operations', 'Печать ярлыка на ПВЗ'),
                                                                                                    (31, 'PVZ_DELIVERY_COMPLETE', 'Pvz Operations', 'Завершение выдачи посылки на ПВЗ'),
                                                                                                    (32, 'PVZ_RECIPIENT_VERIFY', 'Pvz Operations', 'Верификация получателя на ПВЗ'),
                                                                                                    (33, 'PVZ_DISPUTE_RESOLVE', 'Pvz Operations', 'Разрешение спорной ситуации на ПВЗ')
    ON CONFLICT (action_type_id) DO NOTHING;

-- Категория: Payments
INSERT INTO shared_data.audit_action_types (action_type_id, action_name, category, description) VALUES
                                                                                                    (34, 'PAYMENT_INITIATE_CARD', 'Payments', 'Инициация карточного платежа'),
                                                                                                    (35, 'PAYMENT_INITIATE_CASH', 'Payments', 'Открытие кассовой сессии для приема наличных'),
                                                                                                    (36, 'PAYMENT_CONFIRM', 'Payments', 'Подтверждение успешного платежа'),
                                                                                                    (37, 'PAYMENT_FINALIZE_CASH', 'Payments', 'Фиксация приема наличных'),
                                                                                                    (38, 'PAYMENT_REFUND', 'Payments', 'Оформление возврата средств'),
                                                                                                    (39, 'PAYMENT_METHOD_CHANGE', 'Payments', 'Корректировка метода оплаты'),
                                                                                                    (40, 'PAYMENT_DISPUTE_CREATE', 'Payments', 'Создание спора по платежу'),
                                                                                                    (41, 'PAYMENT_DISPUTE_RESOLVE', 'Payments', 'Разрешение платежного спора')
    ON CONFLICT (action_type_id) DO NOTHING;

-- Категория: Warehouse
INSERT INTO shared_data.audit_action_types (action_type_id, action_name, category, description) VALUES
                                                                                                    (42, 'WAREHOUSE_INTAKE', 'Warehouse', 'Регистрация поступления на склад'),
                                                                                                    (43, 'WAREHOUSE_ITEM_SCAN', 'Warehouse', 'Сканирование грузового места на складе'),
                                                                                                    (44, 'WAREHOUSE_SHIPMENT_CREATE', 'Warehouse', 'Создание отгрузки со склада'),
                                                                                                    (45, 'WAREHOUSE_VEHICLE_ASSIGN', 'Warehouse', 'Назначение ТС/водителя на отгрузку'),
                                                                                                    (46, 'WAREHOUSE_LOAD_ITEM', 'Warehouse', 'Погрузка грузового места в ТС'),
                                                                                                    (47, 'WAREHOUSE_SHIPMENT_DEPART', 'Warehouse', 'Фиксация выезда ТС со склада'),
                                                                                                    (48, 'WAREHOUSE_SHIPMENT_ARRIVE', 'Warehouse', 'Фиксация прибытия ТС на склад'),
                                                                                                    (49, 'WAREHOUSE_SHIPMENT_CLOSE', 'Warehouse', 'Закрытие рейса/отгрузки')
    ON CONFLICT (action_type_id) DO NOTHING;

-- Категория: Delivery
INSERT INTO shared_data.audit_action_types (action_type_id, action_name, category, description) VALUES
                                                                                                    (50, 'COURIER_ROUTE_START', 'Delivery', 'Старт маршрутного листа курьера'),
                                                                                                    (51, 'COURIER_GPS_UPDATE', 'Delivery', 'Обновление GPS координат курьера'),
                                                                                                    (52, 'COURIER_PAYMENT_COLLECT', 'Delivery', 'Прием оплаты курьером у получателя'),
                                                                                                    (53, 'COURIER_DELIVERY_COMPLETE', 'Delivery', 'Успешное завершение доставки курьером'),
                                                                                                    (54, 'COURIER_DELIVERY_FAILED', 'Delivery', 'Неудачная попытка доставки курьером'),
                                                                                                    (55, 'COURIER_RECIPIENT_VERIFY', 'Delivery', 'Верификация получателя курьером')
    ON CONFLICT (action_type_id) DO NOTHING;

-- Категория: HR
INSERT INTO shared_data.audit_action_types (action_type_id, action_name, category, description) VALUES
                                                                                                    (56, 'EMPLOYEE_CREATE', 'HR', 'Создание карточки нового сотрудника'),
                                                                                                    (57, 'EMPLOYEE_UPDATE', 'HR', 'Изменение данных сотрудника'),
                                                                                                    (58, 'EMPLOYEE_POSITION_CHANGE', 'HR', 'Изменение должности сотрудника'),
                                                                                                    (59, 'EMPLOYEE_TERMINATE', 'HR', 'Увольнение сотрудника'),
                                                                                                    (60, 'EMPLOYEE_ROLE_SYNC', 'HR', 'Синхронизация роли доступа с должностью')
    ON CONFLICT (action_type_id) DO NOTHING;

-- Категория: Accounting
INSERT INTO shared_data.audit_action_types (action_type_id, action_name, category, description) VALUES
                                                                                                    (61, 'SALARY_UPDATE', 'Accounting', 'Изменение оклада или схемы начислений'),
                                                                                                    (62, 'PAYROLL_GENERATE', 'Accounting', 'Формирование расчетной ведомости'),
                                                                                                    (63, 'PAYOUT_CALCULATE', 'Accounting', 'Расчет выплат самозанятым/контрагентам'),
                                                                                                    (64, 'PAYOUT_CONFIRM', 'Accounting', 'Подтверждение выплаты'),
                                                                                                    (65, 'PAYOUT_ADJUSTMENT', 'Accounting', 'Корректировка суммы выплаты или удержание'),
                                                                                                    (66, 'RECONCILIATION_CREATE', 'Accounting', 'Создание акта сверки по платежам/кассе'),
                                                                                                    (67, 'RECONCILIATION_DISPUTE', 'Accounting', 'Эскалация расхождения в акте сверки')
    ON CONFLICT (action_type_id) DO NOTHING;

-- Категория: System
INSERT INTO shared_data.audit_action_types (action_type_id, action_name, category, description) VALUES
                                                                                                    (68, 'SYSTEM_CONFIG_CHANGE', 'System', 'Изменение глобальной конфигурации системы'),
                                                                                                    (69, 'PERMISSION_GRANT', 'System', 'Выдача расширенного разрешения пользователю'),
                                                                                                    (70, 'PERMISSION_REVOKE', 'System', 'Отзыв расширенного разрешения'),
                                                                                                    (71, 'AUDIT_LOG_VIEW', 'System', 'Просмотр журнала аудита'),
                                                                                                    (72, 'SYSTEM_INTEGRATION_TOGGLE', 'System', 'Включение/выключение внешней интеграции'),
                                                                                                    (73, 'DATA_EXPORT', 'System', 'Экспорт данных из системы'),
                                                                                                    (74, 'DATA_ARCHIVE', 'System', 'Запуск процесса архивации данных')
    ON CONFLICT (action_type_id) DO NOTHING;

-- Категория: Notifications
INSERT INTO shared_data.audit_action_types (action_type_id, action_name, category, description) VALUES
                                                                                                    (75, 'NOTIFICATION_SUBSCRIBE', 'Notifications', 'Подписка на уведомления'),
                                                                                                    (76, 'NOTIFICATION_UNSUBSCRIBE', 'Notifications', 'Отписка от уведомлений')
    ON CONFLICT (action_type_id) DO NOTHING;

-- Категория: Ratings
INSERT INTO shared_data.audit_action_types (action_type_id, action_name, category, description) VALUES
    (77, 'RATING_SUBMIT', 'Ratings', 'Оставление оценки или отзыва')
    ON CONFLICT (action_type_id) DO NOTHING;

-- Категория: Tracking
INSERT INTO shared_data.audit_action_types (action_type_id, action_name, category, description) VALUES
                                                                                                    (78, 'TRACKING_VIEW_PUBLIC', 'Tracking', 'Публичный просмотр статуса трекинга'),
                                                                                                    (79, 'TRACKING_ETA_UPDATE', 'Tracking', 'Обновление прогнозируемого времени прибытия (ETA)'),
                                                                                                    (80, 'GEOLOCATION_UPDATE', 'Tracking', 'Передача GPS-координат курьером/водителем')
    ON CONFLICT (action_type_id) DO NOTHING;
