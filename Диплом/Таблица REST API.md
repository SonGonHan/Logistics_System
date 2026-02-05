## 1. Регистрация и авторизация пользователя

| Порядок | Действие | Метод | Endpoint | Сервис | Body/Параметры | Ответ |
|---------|----------|-------|----------|--------|----------------|-------|
| 1 | Создание учетной записи | POST | `/api/v1/auth/register` | User Management Service | `{"phone_number": "+7...", "email": "user@example.com", "full_name": "ФИ"}` | `{"user_id": "uuid", "status": "created"}` |
| 2 | Авторизация пользователя | POST | `/api/v1/auth/login` | User Management Service | `{"phone_number": "+7...", "password": "***"}` | `{"access_token": "jwt_token", "refresh_token": "jwt_token"}` |

---

## 2. Создание черновика накладной

| Порядок | Действие | Метод | Endpoint | Сервис | Body/Параметры | Ответ |
|---------|----------|-------|----------|--------|----------------|-------|
| 1 | Калькуляция стоимости и сроков | POST | `/api/v1/pricing/calculate` | Pricing Service | `{"from_city": "Москва", "to_city": "СПб", "weight": 1.5, "dimensions": {...}}` | `{"cost": 500, "delivery_days": 3}` |
| 2 | Сохранение черновика накладной | POST | `/api/v1/waybills/drafts` | Waybill Service | `{"sender": {...}, "recipient": {...}, "items": [...]}` | `{"draft_id": "uuid", "barcode": "123456", "status": "draft"}` |
| 3 | Установка плательщика и метода оплаты | PUT | `/api/v1/waybills/drafts/{draft_id}/payment` | Waybill Service | `{"payer": "sender", "payment_method": "card"}` | `{"draft_id": "uuid", "updated": true}` |
| 4 | Изменение выбора плательщика/метода | PUT | `/api/v1/waybills/drafts/{draft_id}/payment` | Waybill Service | `{"payer": "recipient", "payment_method": "cash"}` | `{"draft_id": "uuid", "updated": true}` |
| 5 | Редактирование/отмена черновика | DELETE | `/api/v1/waybills/drafts/{draft_id}` | Waybill Service | — | `{"draft_id": "uuid", "status": "cancelled"}` |

---

## 3. Сдача посылки в ПВЗ по черновику накладной

| Порядок | Действие | Метод | Endpoint | Сервис | Body/Параметры | Ответ |
|---------|----------|-------|----------|--------|----------------|-------|
| 1 | Поиск черновика по штрихкоду/ID | GET | `/api/v1/waybills/drafts/{barcode}` | Waybill Service | — | `{"draft_id": "uuid", "sender": {...}, "recipient": {...}}` |
| 2 | Регистрация фактических параметров и фотофиксация | PUT | `/api/v1/acceptance/register/{draft_id}` | Acceptance Service | `{"actual_weight": 1.8, "actual_dimensions": {...}, "photo_ids": ["id1","id2"]}` | `{"waybill_id": "uuid", "recalculated_cost": 550}` |
| 3 | Проверка статуса оплаты | GET | `/api/v1/payments/status/{draft_id}` | Payment Service | — | `{"payer": "sender", "payment_method": "card", "status": "pending"}` |
| 4 | Инициация оплаты через эквайринг | POST | `/api/v1/payments/initiate-card` | Payment Service | `{"waybill_id": "uuid", "amount": 550, "terminal_id": "term123"}` | `{"payment_id": "uuid", "status": "processing"}` |
| 5 | Подтверждение оплаты и печать чека | POST | `/api/v1/payments/confirm/{payment_id}` | Payment Service | `{"check_format": "thermal"}` | `{"payment_id": "uuid", "status": "completed"}` |
| 6 | Открытие приема наличных | POST | `/api/v1/payments/initiate-cash` | Payment Service | `{"waybill_id": "uuid", "amount_required": 550}` | `{"payment_id": "uuid", "status": "awaiting_cash"}` |
| 7 | Фиксация получено/сдано (наличные) | POST | `/api/v1/payments/finalize-cash/{payment_id}` | Payment Service | `{"received_amount": 550, "change": 0}` | `{"payment_id": "uuid", "status": "completed", "change": 0}` |
| 8 | Пометка ожидания оплаты получателем | PUT | `/api/v1/waybills/{draft_id}/payment-mode` | Waybill Service | `{"mode": "collect_on_delivery"}` | `{"draft_id": "uuid", "payment_mode": "cod"}` |
| 9 | Подтверждение условий получателем | POST | `/api/v1/waybills/{draft_id}/confirm` | Waybill Service | `{"confirmed": true, "signature": "..."}` | `{"draft_id": "uuid", "status": "confirmed"}` |
| 10 | Оформление отправки и печать ярлыка | POST | `/api/v1/waybills/finalize/{draft_id}` | Waybill Service | `{"print_label": true, "print_receipt": true}` | `{"waybill_id": "uuid", "status": "accepted_at_pvz", "label_url": "..."}` |
| 11 | Добавление дополнительных услуг/упаковки | POST | `/api/v1/waybills/{draft_id}/services` | Acceptance Service | `{"service_code": "box", "quantity": 1, "price": 50}` | `{"service_id": "uuid", "total_cost": 600}` |

---

## 4. Приемка в ПВЗ без предварительного черновика

| Порядок | Действие | Метод | Endpoint | Сервис | Body/Параметры | Ответ |
|---------|----------|-------|----------|--------|----------------|-------|
| 1 | Создание черновика из ПВЗ | POST | `/api/v1/waybills/drafts/from-counter` | Waybill Service | `{"sender_data": {...}, "recipient_data": {...}, "items": [...]}` | `{"draft_id": "uuid", "barcode": "654321", "status": "draft"}` |
| 2 | Установка плательщика и метода оплаты | PUT | `/api/v1/waybills/drafts/{draft_id}/payment` | Waybill Service | `{"payer": "sender", "payment_method": "card"}` | `{"draft_id": "uuid", "updated": true}` |
| 3 | Проверка веса/габаритов | POST | `/api/v1/acceptance/verify/{draft_id}` | Acceptance Service | `{"weight": 2.0, "dimensions": {"length": 30, "width": 20, "height": 10}}` | `{"verified": true, "recalculated_cost": 600}` |
| 4 | Подтверждение условий отправления | POST | `/api/v1/waybills/{draft_id}/confirm` | Waybill Service | `{"confirmed": true}` | `{"draft_id": "uuid", "status": "confirmed"}` |
| 5 | Конвертация черновика в накладную | POST | `/api/v1/waybills/finalize/{draft_id}` | Waybill Service | `{"print_label": true}` | `{"waybill_id": "uuid", "status": "accepted_at_pvz"}` |
| 6 | Добавление услуг/упаковки | POST | `/api/v1/waybills/{draft_id}/services` | Acceptance Service | `{"service_code": "packaging", "quantity": 1}` | `{"service_id": "uuid", "total_cost": 650}` |

---

## 5. Отслеживание статуса и уведомления

| Порядок | Действие | Метод | Endpoint | Сервис | Body/Параметры | Ответ |
|---------|----------|-------|----------|--------|----------------|-------|
| 1 | Получение статуса заказа/черновика | GET | `/api/v1/tracking/{waybill_id}` | Tracking Service | — | `{"waybill_id": "uuid", "status": "in_transit", "eta": "2025-11-01T15:00Z"}` |
| 2 | Подписка на уведомления | POST | `/api/v1/notifications/subscribe` | Notification Service | `{"waybill_id": "uuid", "channels": ["email", "sms", "push"]}` | `{"subscription_id": "uuid", "status": "active"}` |
| 3 | Получение истории статусов и ETA | GET | `/api/v1/tracking/{waybill_id}/history` | Tracking Service | — | `{"events": [...], "eta_updated": "2025-11-01T15:00Z"}` |
| 4 | Ручное обновление прогноза ETA | PUT | `/api/v1/tracking/{waybill_id}/eta` | Tracking Service | `{"new_eta": "2025-11-01T17:00Z", "reason": "traffic"}` | `{"eta": "2025-11-01T17:00Z", "updated": true}` |

---

## 6. Отслеживание посылки неавторизованным получателем

| Порядок | Действие | Метод | Endpoint | Сервис | Body/Параметры | Ответ |
|---------|----------|-------|----------|--------|----------------|-------|
| 1 | Проверка номера накладной и телефона | POST | `/api/v1/tracking/public/verify` | Tracking Service | `{"waybill_number": "WB-123456", "phone_number": "+7..."}` | `{"verified": true, "waybill_id": "uuid"}` |
| 2 | Получение публичного статуса посылки | GET | `/api/v1/tracking/public/{waybill_id}` | Tracking Service | — | `{"status": "in_delivery", "eta": "2025-11-01T16:00Z", "events": [...]}` |
| 3 | Получение информации об оплате и способах | GET | `/api/v1/tracking/public/{waybill_id}/payment-info` | Payment Service | — | `{"amount": 550, "payment_required": true, "methods": ["cash", "card"]}` |

---

## 7. Отмена до сдачи и возвраты

| Порядок | Действие | Метод | Endpoint | Сервис | Body/Параметры | Ответ |
|---------|----------|-------|----------|--------|----------------|-------|
| 1 | Отмена черновика до сдачи | DELETE | `/api/v1/waybills/drafts/{draft_id}` | Waybill Service | — | `{"draft_id": "uuid", "status": "cancelled"}` |
| 2 | Оформление возврата платежа | POST | `/api/v1/payments/refund` | Payment Service | `{"payment_id": "uuid", "reason": "user_request"}` | `{"refund_id": "uuid", "status": "processed"}` |
| 3 | Остановка обработки и возврат | POST | `/api/v1/waybills/{waybill_id}/stop-processing` | Waybill Service | `{"reason": "customer_request"}` | `{"waybill_id": "uuid", "status": "stopped"}` |
| 4 | Оформление возврата/переадресации | POST | `/api/v1/waybills/{waybill_id}/process-return` | Waybill Service | `{"action": "reroute", "new_recipient": {...}}` | `{"return_id": "uuid", "status": "initiated"}` |

---

## 8. Складская приемка и отгрузка

| Порядок | Действие | Метод | Endpoint | Сервис | Body/Параметры | Ответ |
|---------|----------|-------|----------|--------|----------------|-------|
| 1 | Регистрация поступления на склад | POST | `/api/v1/warehouse/intake` | Warehouse Service | `{"waybill_ids": ["id1", "id2"], "scanned_barcodes": ["code1", "code2"]}` | `{"intake_id": "uuid", "items_received": 2}` |
| 2 | Создание отгрузки из склада | POST | `/api/v1/warehouse/shipments` | Warehouse Service | `{"destination_pvz": "pvz_moscow", "items": [...]}` | `{"shipment_id": "uuid", "status": "created"}` |
| 3 | Назначение ТС/водителя и слота погрузки | PUT | `/api/v1/warehouse/shipments/{shipment_id}/assign` | Warehouse Service | `{"vehicle_id": "v123", "driver_id": "d456", "loading_slot": 1}` | `{"shipment_id": "uuid", "assigned": true}` |
| 4 | Подтверждение погрузки мест | POST | `/api/v1/warehouse/shipments/{shipment_id}/load-item` | Warehouse Service | `{"waybill_id": "uuid", "barcode": "scan123"}` | `{"loaded": true, "total_loaded": 5}` |
| 5 | Фиксация выезда со склада | POST | `/api/v1/warehouse/shipments/{shipment_id}/depart` | Warehouse Service | `{"departure_time": "2025-11-01T10:30:00Z", "vehicle_gps": {...}}` | `{"shipment_id": "uuid", "status": "departed"}` |
| 6 | Мониторинг статусов в реальном времени | GET | `/api/v1/warehouse/shipments/{shipment_id}/telemetry` | Geolocation Service | — | `{"current_location": {...}, "eta": "2025-11-01T15:00Z", "status": "in_transit"}` |

---

## 9. Получение посылки неавторизованным контактом на ПВЗ

| Порядок | Действие | Метод | Endpoint | Сервис | Body/Параметры | Ответ |
|---------|----------|-------|----------|--------|----------------|-------|
| 1 | Поиск накладной по номеру или телефону | GET | `/api/v1/waybills/search` | Waybill Service | `query_params: waybill_number=WB-123456 OR phone=+7...` | `{"waybill_id": "uuid", "recipient_name": "...", "phone": "+7..."}` |
| 2 | Верификация личности получателя | POST | `/api/v1/waybills/{waybill_id}/verify-recipient` | Waybill Service | `{"document_type": "passport", "document_data": {...}}` | `{"verified": true, "recipient_id": "uuid"}` |
| 3 | Проверка статуса оплаты при получении | GET | `/api/v1/payments/status/{waybill_id}` | Payment Service | — | `{"payment_required": true, "amount": 550, "method": "cash"}` |
| 4 | Инициация оплаты через эквайринг | POST | `/api/v1/payments/initiate-card` | Payment Service | `{"waybill_id": "uuid", "amount": 550}` | `{"payment_id": "uuid", "status": "processing"}` |
| 5 | Подтверждение оплаты | POST | `/api/v1/payments/confirm/{payment_id}` | Payment Service | `{"check_format": "thermal"}` | `{"payment_id": "uuid", "status": "completed"}` |
| 6 | Открытие приема наличных | POST | `/api/v1/payments/initiate-cash` | Payment Service | `{"waybill_id": "uuid", "amount_required": 550}` | `{"payment_id": "uuid", "status": "awaiting_cash"}` |
| 7 | Фиксация получено/сдано (наличные) | POST | `/api/v1/payments/finalize-cash/{payment_id}` | Payment Service | `{"received_amount": 550, "change": 0}` | `{"status": "completed", "change": 0}` |
| 8 | Завершение выдачи | POST | `/api/v1/waybills/{waybill_id}/complete-delivery` | Waybill Service | `{"recipient_id": "uuid", "confirmation_method": "signature"}` | `{"waybill_id": "uuid", "status": "delivered"}` |
| 9 | Фиксация факта выдачи | POST | `/api/v1/audit/log-delivery` | Audit Service | `{"waybill_id": "uuid", "timestamp": "...", "recipient_id": "uuid"}` | `{"log_id": "uuid", "recorded": true}` |

---

## 10. Доставка и завершение (курьер)

| Порядок | Действие | Метод | Endpoint | Сервис | Body/Параметры | Ответ |
|---------|----------|-------|----------|--------|----------------|-------|
| 1 | Получение списка назначений и маршрута | GET | `/api/v1/delivery/courier/assignments` | Delivery Service | `query_params: courier_id=c123&date=2025-11-01` | `{"assignments": [...], "route": {...}, "change_required": {"total": 5000}}` |
| 2 | Фиксация старта рейса | POST | `/api/v1/delivery/courier/start-route` | Delivery Service | `{"courier_id": "c123", "start_time": "...", "start_gps": {...}}` | `{"route_id": "uuid", "status": "started"}` |
| 3 | Проверка статусов оплаты перед выдачей | GET | `/api/v1/payments/batch-status` | Payment Service | `query_params: waybill_ids=["id1","id2",...]` | `{"items": [{"waybill_id": "uuid", "status": "pending"}]}` |
| 4 | Инициация оплаты на мобильном терминале | POST | `/api/v1/payments/mobile-terminal/initiate-card` | Payment Service | `{"waybill_id": "uuid", "amount": 550, "terminal_id": "t789"}` | `{"payment_id": "uuid", "status": "awaiting_card"}` |
| 5 | Пометка оплаты выполненной | POST | `/api/v1/payments/confirm/{payment_id}` | Payment Service | `{}` | `{"payment_id": "uuid", "status": "completed"}` |
| 6 | Открытие приема наличных | POST | `/api/v1/payments/initiate-cash` | Payment Service | `{"waybill_id": "uuid", "amount_required": 550}` | `{"payment_id": "uuid", "status": "awaiting_cash"}` |
| 7 | Фиксация получено/сдано (наличные) | POST | `/api/v1/payments/finalize-cash/{payment_id}` | Payment Service | `{"received_amount": 550, "change": 0}` | `{"status": "completed", "change": 0}` |
| 8 | Передача GPS координат | POST | `/api/v1/geolocation/update-position` | Geolocation Service | `{"courier_id": "c123", "latitude": 55.75, "longitude": 37.62, "timestamp": "..."}` | `{"position_id": "uuid", "recorded": true}` |
| 9 | Обновление промежуточных статусов | PUT | `/api/v1/delivery/courier/{waybill_id}/status` | Delivery Service | `{"status": "in_transit", "note": "On the way"}` | `{"waybill_id": "uuid", "status": "updated"}` |
| 10 | Подтверждение успешной доставки | POST | `/api/v1/delivery/courier/{waybill_id}/complete` | Delivery Service | `{"recipient_signature": "...", "photo_proof": "..."}` | `{"waybill_id": "uuid", "status": "delivered"}` |
| 11 | Регистрация неудачной попытки | POST | `/api/v1/delivery/courier/{waybill_id}/failed-attempt` | Delivery Service | `{"reason": "recipient_absent", "retry_date": "2025-11-02"}` | `{"waybill_id": "uuid", "status": "failed_attempt"}` |

---

## 11. Получение посылки неавторизованным контактом от курьера

| Порядок | Действие | Метод | Endpoint | Сервис | Body/Параметры | Ответ |
|---------|----------|-------|----------|--------|----------------|-------|
| 1 | Верификация личности получателя | POST | `/api/v1/delivery/courier/verify-recipient` | Delivery Service | `{"phone_number": "+7...", "waybill_id": "uuid"}` | `{"verified": true, "recipient_id": "uuid"}` |
| 2 | Проверка статуса оплаты | GET | `/api/v1/payments/status/{waybill_id}` | Payment Service | — | `{"payment_required": true, "amount": 550}` |
| 3 | Оплата через мобильный терминал | POST | `/api/v1/payments/mobile-terminal/initiate-card` | Payment Service | `{"waybill_id": "uuid", "amount": 550, "terminal_id": "t789"}` | `{"payment_id": "uuid", "status": "awaiting_card"}` |
| 4 | Подтверждение карточного платежа | POST | `/api/v1/payments/confirm/{payment_id}` | Payment Service | `{}` | `{"payment_id": "uuid", "status": "completed"}` |
| 5 | Открытие приема наличных | POST | `/api/v1/payments/initiate-cash` | Payment Service | `{"waybill_id": "uuid", "amount_required": 550}` | `{"payment_id": "uuid", "status": "awaiting_cash"}` |
| 6 | Фиксация получено/сдано (наличные) | POST | `/api/v1/payments/finalize-cash/{payment_id}` | Payment Service | `{"received_amount": 550, "change": 0}` | `{"status": "completed", "change": 0}` |
| 7 | Завершение доставки | POST | `/api/v1/delivery/courier/{waybill_id}/complete` | Delivery Service | `{"recipient_id": "uuid", "gps_coords": {...}, "timestamp": "..."}` | `{"waybill_id": "uuid", "status": "delivered"}` |
| 8 | Отправка SMS с предложением регистрации | POST | `/api/v1/notifications/send-registration-offer` | Notification Service | `{"phone_number": "+7...", "waybill_id": "uuid"}` | `{"notification_id": "uuid", "status": "sent"}` |
| 9 | Регистрация неудачной попытки | POST | `/api/v1/delivery/courier/{waybill_id}/failed-attempt` | Delivery Service | `{"reason": "recipient_absent"}` | `{"waybill_id": "uuid", "status": "failed_attempt"}` |

---

## 12. Доставка и завершение (водитель)

| Порядок | Действие | Метод | Endpoint | Сервис | Body/Параметры | Ответ |
|---------|----------|-------|----------|--------|----------------|-------|
| 1 | Получение плана рейса | GET | `/api/v1/delivery/driver/routes` | Delivery Service | `query_params: driver_id=d456&date=2025-11-01` | `{"routes": [{...}], "status": "assigned"}` |
| 2 | Фиксация прибытия на склад | POST | `/api/v1/warehouse/shipments/{shipment_id}/arrival` | Warehouse Service | `{"driver_id": "d456", "arrival_time": "...", "vehicle_id": "v123"}` | `{"shipment_id": "uuid", "status": "ready_for_loading"}` |
| 3 | Подтверждение погрузки места | POST | `/api/v1/warehouse/shipments/{shipment_id}/load-item` | Warehouse Service | `{"waybill_id": "uuid", "barcode": "scan123"}` | `{"loaded": true, "total_loaded": 5}` |
| 4 | Формирование акта погрузки | POST | `/api/v1/warehouse/shipments/{shipment_id}/loading-document` | Warehouse Service | `{"total_items": 5, "total_weight": 10.5}` | `{"document_id": "uuid", "status": "generated"}` |
| 5 | Выезд со склада/прибытие на место | POST | `/api/v1/warehouse/shipments/{shipment_id}/milestone` | Warehouse Service | `{"milestone": "departed", "timestamp": "...", "gps": {...}}` | `{"milestone_id": "uuid", "recorded": true}` |
| 6 | Разгрузка и закрытие рейса | POST | `/api/v1/warehouse/shipments/{shipment_id}/close` | Warehouse Service | `{"metrics": {"duration": 180, "distance": 45, "deviations": []}}` | `{"shipment_id": "uuid", "status": "closed"}` |

---

## 13. Оценка и отзыв

| Порядок | Действие | Метод | Endpoint | Сервис | Body/Параметры | Ответ |
|---------|----------|-------|----------|--------|----------------|-------|
| 1 | Предложение оценки после доставки | GET | `/api/v1/ratings/request/{waybill_id}` | Rating Service | — | `{"waybill_id": "uuid", "can_rate": true}` |
| 2 | Сохранение рейтинга и отзыва | POST | `/api/v1/ratings/submit` | Rating Service | `{"waybill_id": "uuid", "rating": 5, "comment": "Excellent service!"}` | `{"rating_id": "uuid", "recorded": true}` |

---

## 14. Регистрация неавторизованного контакта в системе

| Порядок | Действие                                | Метод | Endpoint                                    | Сервис                  | Body/Параметры                                                       | Ответ                                                    |
| ------- | --------------------------------------- | ----- | ------------------------------------------- | ----------------------- | -------------------------------------------------------------------- | -------------------------------------------------------- |
| 1       | Проверка существования контакта         | POST  | `/api/v1/auth/check-contact`                | User Management Service | `{"phone_number": "+7..."}`                                          | `{"exists": true, "user_type": "UNREGISTERED_CONTACT"}`  |
| 2       | Верификация телефона                    | POST  | `/api/v1/auth/verify-phone`                 | User Management Service | `{"phone_number": "+7...", "verification_code": "123456"}`           | `{"verified": true, "token": "temp_token"}`              |
| 3       | Обновление пользователя при регистрации | PUT   | `/api/v1/users/{user_id}/upgrade-to-client` | User Management Service | `{"password": "***", "email": "user@example.com", "role": "CLIENT"}` | `{"user_id": "uuid", "role": "CLIENT", "updated": true}` |
| 4       | Первый вход новым клиентом              | POST  | `/api/v1/auth/login`                        | User Management Service | `{"phone_number": "+7...", "password": "***"}`                       | `{"access_token": "jwt_token", "user_role": "CLIENT"}`   |

---

## 15. HR, бухгалтерия и интеграция

| Порядок | Действие | Метод | Endpoint | Сервис | Body/Параметры | Ответ |
|---------|----------|-------|----------|--------|----------------|-------|
| 1 | Создание карточки сотрудника | POST | `/api/v1/hr/employees` | HR Service | `{"full_name": "...", "position": "manager", "hire_date": "2025-11-01"}` | `{"employee_id": "uuid", "status": "active"}` |
| 2 | Изменение должности/статуса | PUT | `/api/v1/hr/employees/{employee_id}` | HR Service | `{"position": "senior_manager", "status": "active"}` | `{"employee_id": "uuid", "updated": true}` |
| 3 | Оформление увольнения | DELETE | `/api/v1/hr/employees/{employee_id}` | HR Service | `{"reason": "resignation", "termination_date": "2025-11-30"}` | `{"employee_id": "uuid", "status": "terminated"}` |
| 4 | Изменение окладов/начислений | PUT | `/api/v1/accounting/salaries/{employee_id}` | Accounting Service | `{"salary": 100000, "bonus": 10000, "effective_date": "2025-12-01"}` | `{"salary_record_id": "uuid", "updated": true}` |
| 5 | Формирование расчетных ведомостей | GET | `/api/v1/accounting/payroll` | Accounting Service | `query_params: period=2025-11&department=delivery` | `{"payroll": [...], "total": 500000, "status": "generated"}` |
| 6 | Выгрузка финансовых отчетов | GET | `/api/v1/accounting/reports/export` | Accounting Service | `query_params: report_type=financial&period=2025-11` | `{"report_id": "uuid", "format": "xlsx", "download_url": "..."}` |
| 7 | Сверка платежей и кассовых смен | POST | `/api/v1/accounting/reconciliation` | Accounting Service | `{"pvz_id": "pvz123", "period": "2025-11-01"}` | `{"reconciliation_id": "uuid", "discrepancies": []}` |
| 8 | Обработка расхождений | POST | `/api/v1/accounting/reconciliation/dispute` | Accounting Service | `{"discrepancy_id": "uuid", "action": "escalate"}` | `{"dispute_id": "uuid", "status": "escalated_to_admin"}` |
| 9 | Доступ к административным журналам | GET | `/api/v1/audit/logs` | Audit Service | `query_params: user_id=u123&start_date=2025-11-01&end_date=2025-11-30` | `{"logs": [...], "total_records": 500}` |

---

## 16. Курьерские и водительские выплаты

| Порядок | Действие | Метод | Endpoint | Сервис | Body/Параметры | Ответ |
|---------|----------|-------|----------|--------|----------------|-------|
| 1 | Инициация расчета выплат | POST | `/api/v1/payouts/calculate` | Payout Service | `{"courier_id": "c123", "period_start": "2025-11-01", "period_end": "2025-11-30"}` | `{"calculation_id": "uuid", "total_amount": 50000}` |
| 2 | Подтверждение и отправка на выплату | POST | `/api/v1/payouts/confirm` | Payout Service | `{"calculation_id": "uuid", "payment_method": "bank_transfer"}` | `{"payout_id": "uuid", "status": "initiated"}` |
| 3 | Проверка статусов начислений | GET | `/api/v1/payouts/status` | Payout Service | `query_params: courier_id=c123&period=2025-11` | `{"payouts": [...], "total": 50000}` |
| 4 | Оформление корректировок и удержаний | POST | `/api/v1/payouts/adjustment` | Payout Service | `{"courier_id": "c123", "type": "deduction", "amount": 1000, "reason": "damage_claim"}` | `{"adjustment_id": "uuid", "recorded": true}` |

---

## 17. Интеграция через пограничные сервисы

| Порядок | Действие | Метод | Endpoint | Сервис | Body/Параметры | Ответ |
|---------|----------|-------|----------|--------|----------------|-------|
| 1 | Публичный API заказов | POST | `/api/v1/orders/create` | Edge Gateway | `{"sender": {...}, "recipient": {...}, "items": [...]}` | `{"order_id": "uuid", "status": "created"}` |
| 2 | Маршрутизация к внутреннему сервису | POST | `/internal/waybills/create` | Waybill Service | `{"sender": {...}, "recipient": {...}, "items": [...]}` | `{"waybill_id": "uuid", "status": "created"}` |
| 3 | Агрегирование истории/ETA | GET | `/internal/tracking/aggregate/{waybill_id}` | Tracking Service | — | `{"status": "in_transit", "eta": "2025-11-01T16:00Z", "events": [...]}` |
| 4 | Уведомления при создании/изменении | POST | `/internal/notifications/queue` | Notification Service | `{"event": "order_created", "waybill_id": "uuid", "priority": "high"}` | `{"notification_id": "uuid", "queued": true}` |
| 5 | Публичные версии и rate limiting | GET | `/api/v1/status/health` | Edge Gateway | — | `{"status": "healthy", "version": "1.2.3"}` |
| 6 | Админ-панель HR | GET | `/api/v1/admin/employees` | Edge Gateway | `header: Authorization: Bearer admin_token` | `{"employees": [...], "total": 50}` |
| 7 | Маршрутизация к HR-сервису | GET | `/internal/hr/employees` | HR Service | — | `{"employees": [...], "total": 50}` |
| 8 | Фиксация аудита действий | POST | `/internal/audit/log` | Audit Service | `{"action": "employee_list_viewed", "admin_id": "a123", "timestamp": "..."}` | `{"log_id": "uuid", "recorded": true}` |
| 9 | Админ-панель бухгалтера | GET | `/api/v1/admin/accounting/reports` | Edge Gateway | `query_params: period=2025-11` | `{"reports": [...], "total_revenue": 1000000}` |
| 10 | Маршрутизация к сервису бухгалтерии | GET | `/internal/accounting/reports` | Accounting Service | `query_params: period=2025-11` | `{"reports": [...], "total_revenue": 1000000}` |
| 11 | Маршрутизация к сервису выплат | GET | `/internal/payouts/status` | Payout Service | `query_params: period=2025-11` | `{"payouts": [...], "total_amount": 500000}` |
| 12 | Получение заданий курьером | GET | `/api/v1/delivery/courier/assignments` | Edge Gateway | `header: Authorization: Bearer courier_token` | `{"assignments": [...], "route": {...}}` |
| 13 | Отправка GPS координат | POST | `/api/v1/geolocation/track` | Edge Gateway | `{"courier_id": "c123", "latitude": 55.75, "longitude": 37.62}` | `{"position_id": "uuid", "recorded": true}` |
| 14 | Завершение доставки | POST | `/api/v1/delivery/courier/complete` | Edge Gateway | `{"waybill_id": "uuid", "signature": "...", "photo": "..."}` | `{"waybill_id": "uuid", "status": "delivered"}` |
| 15 | Получение рейсов водителем | GET | `/api/v1/delivery/driver/routes` | Edge Gateway | `header: Authorization: Bearer driver_token` | `{"routes": [...], "status": "assigned"}` |
| 16 | Фиксация погрузки/выезда/прибытия | POST | `/api/v1/warehouse/milestones` | Edge Gateway | `{"shipment_id": "uuid", "milestone": "departed", "gps": {...}}` | `{"milestone_id": "uuid", "recorded": true}` |
| 17 | Закрытие рейса | POST | `/api/v1/warehouse/shipments/close` | Edge Gateway | `{"shipment_id": "uuid", "metrics": {...}}` | `{"shipment_id": "uuid", "status": "closed"}` |
| 18 | Обработка ошибок и fallback | GET | `/api/v1/error/handle` | Edge Gateway | — | `{"error": "Service unavailable", "fallback_data": {...}}` |
| 19 | Отправка телеметрии отказов | POST | `/internal/telemetry/failures` | Telemetry Service | `{"service": "waybill_service", "error": "timeout", "timestamp": "..."}` | `{"telemetry_id": "uuid", "recorded": true}` |
| 20 | Метрики и мониторинг | GET | `/api/v1/metrics/dashboard` | Edge Gateway | — | `{"requests_per_second": 1000, "error_rate": 0.01, "latency_ms": 150}` |

---

## Распределение API запросов по сервисам

| Сервис | Количество запросов |
|--------|-------------------|
| Payment Service | 22 |
| Waybill Service | 19 |
| Edge Gateway | 12 |
| Warehouse Service | 10 |
| Delivery Service | 9 |
| User Management Service | 6 |
| Accounting Service | 6 |
| Tracking Service | 6 |
| Payout Service | 5 |
| HR Service | 4 |
| Acceptance Service | 4 |
| Notification Service | 3 |
| Audit Service | 3 |
| Rating Service | 2 |
| Geolocation Service | 2 |
| Pricing Service | 1 |
| Telemetry Service | 1 |

**Всего: 115 API запросов**

---

## Соглашения по API

### HTTP методы
- **GET**: Получение данных без изменения состояния
- **POST**: Создание новых ресурсов
- **PUT**: Обновление существующих ресурсов
- **DELETE**: Удаление ресурсов

### Аутентификация
- Все защищенные endpoints требуют Bearer token в заголовке `Authorization`
- Токены выдаются при успешной авторизации через `/api/v1/auth/login`

### Обработка ошибок
- Edge Gateway нормализует все ошибки и возвращает fallback-ответы
- Технические ошибки отправляются в Telemetry Service

### Версионирование
- Все endpoints версионированы (`/api/v1/`)
- Поддержка canary/blue-green deployments на уровне Edge Gateway

