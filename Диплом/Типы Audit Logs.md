## **1. Аутентификация и авторизация**

|Action|Table Name|Описание|user_id|actor_identifier|
|---|---|---|---|---|
|`USER_REGISTER`|`users`|Регистрация нового пользователя|`user_id`|email/phone|
|`USER_LOGIN_SUCCESS`|`users`|Успешный вход в систему|`user_id`|NULL|
|`USER_LOGIN_FAILURE`|`users`|Неудачная попытка входа|`NULL`|введенный email/phone|
|`USER_LOGOUT`|`users`|Выход из системы|`user_id`|NULL|
|`PASSWORD_CHANGE`|`users`|Смена пароля|`user_id`|NULL|
|`PASSWORD_RESET_REQUEST`|`users`|Запрос сброса пароля|`user_id` или `NULL`|email/phone|
|`SESSION_CREATE`|`user_sessions`|Создание новой сессии|`user_id`|NULL|
|`SESSION_REVOKE`|`user_sessions`|Отзыв сессии (вручную или по истечении)|`user_id`|NULL|
|`TOKEN_REFRESH`|`user_sessions`|Обновление токена доступа|`user_id`|NULL|

---

## **2. Управление пользователями**

|Action|Table Name|Описание|user_id|actor_identifier|
|---|---|---|---|---|
|`USER_CREATE`|`users`|Создание пользователя администратором/HR|`admin_user_id`|NULL|
|`USER_UPDATE`|`users`|Изменение данных пользователя|`user_id` (кто изменил)|NULL|
|`USER_ROLE_CHANGE`|`users`|Изменение роли пользователя|`admin_user_id`|NULL|
|`USER_STATUS_CHANGE`|`users`|Изменение статуса (активный/заблокирован)|`admin_user_id`|NULL|
|`USER_UPGRADE_TO_CLIENT`|`users`|Переход UNREGISTERED_CONTACT → CLIENT|`user_id`|NULL|
|`USER_DELETE`|`users`|Удаление/деактивация пользователя|`admin_user_id`|NULL|
|`USER_FACILITY_CHANGE`|`users`|Изменение привязки к объекту|`admin_user_id`|NULL|

---

## **3. Управление накладными (Waybills)**

|Action|Table Name|Описание|user_id|actor_identifier|
|---|---|---|---|---|
|`DRAFT_CREATE`|`waybills`|Создание черновика накладной|`user_id`|NULL|
|`DRAFT_UPDATE`|`waybills`|Редактирование черновика|`user_id`|NULL|
|`DRAFT_CANCEL`|`waybills`|Отмена черновика|`user_id`|NULL|
|`WAYBILL_FINALIZE`|`waybills`|Конвертация черновика в накладную|`operator_user_id`|NULL|
|`WAYBILL_STATUS_CHANGE`|`waybills`|Изменение статуса накладной|`user_id`|NULL|
|`WAYBILL_PAYMENT_MODE_CHANGE`|`waybills`|Изменение плательщика/метода|`operator_user_id`|NULL|
|`WAYBILL_CONFIRM`|`waybills`|Подтверждение условий|`user_id`|NULL|
|`WAYBILL_CANCEL`|`waybills`|Отмена накладной|`user_id`|NULL|
|`WAYBILL_REROUTE`|`waybills`|Переадресация|`dispatcher_user_id`|NULL|
|`WAYBILL_SERVICE_ADD`|`waybills`|Добавление доп. услуги|`operator_user_id`|NULL|

---

## **4. Операции на ПВЗ**

|Action|Table Name|Описание|user_id|actor_identifier|
|---|---|---|---|---|
|`PVZ_ACCEPTANCE_START`|`waybills`|Начало приемки на ПВЗ|`operator_user_id`|NULL|
|`PVZ_WEIGHT_VERIFY`|`waybills`|Проверка веса/габаритов|`operator_user_id`|NULL|
|`PVZ_PHOTO_UPLOAD`|`waybills`|Загрузка фото посылки|`operator_user_id`|NULL|
|`PVZ_LABEL_PRINT`|`waybills`|Печать ярлыка|`operator_user_id`|NULL|
|`PVZ_DELIVERY_COMPLETE`|`waybills`|Завершение выдачи на ПВЗ|`operator_user_id`|NULL|
|`PVZ_RECIPIENT_VERIFY`|`waybills`|Верификация получателя|`operator_user_id`|NULL|
|`PVZ_DISPUTE_RESOLVE`|`waybills`|Разрешение спорной ситуации|`admin_user_id`|NULL|

---

## **5. Платежи**

|Action|Table Name|Описание|user_id|actor_identifier|
|---|---|---|---|---|
|`PAYMENT_INITIATE_CARD`|`payments`|Инициация карточного платежа|`user_id`|NULL|
|`PAYMENT_INITIATE_CASH`|`payments`|Открытие приема наличных|`operator_user_id`|NULL|
|`PAYMENT_CONFIRM`|`payments`|Подтверждение платежа|`operator_user_id`|NULL|
|`PAYMENT_FINALIZE_CASH`|`payments`|Фиксация получено/сдано|`operator_user_id`|NULL|
|`PAYMENT_REFUND`|`payments`|Оформление возврата|`accountant_user_id`|NULL|
|`PAYMENT_METHOD_CHANGE`|`payments`|Корректировка метода оплаты|`admin_user_id`|NULL|
|`PAYMENT_DISPUTE_CREATE`|`payments`|Создание спора по платежу|`operator_user_id`|NULL|
|`PAYMENT_DISPUTE_RESOLVE`|`payments`|Разрешение платежного спора|`admin_user_id`|NULL|

---

## **6. Складские операции**

|Action|Table Name|Описание|user_id|actor_identifier|
|---|---|---|---|---|
|`WAREHOUSE_INTAKE`|`warehouse`|Регистрация поступления|`operator_user_id`|NULL|
|`WAREHOUSE_ITEM_SCAN`|`warehouse`|Сканирование места|`operator_user_id`|NULL|
|`WAREHOUSE_SHIPMENT_CREATE`|`shipments`|Создание отгрузки|`manager_user_id`|NULL|
|`WAREHOUSE_VEHICLE_ASSIGN`|`shipments`|Назначение ТС/водителя|`manager_user_id`|NULL|
|`WAREHOUSE_LOAD_ITEM`|`shipments`|Погрузка места|`operator_user_id`|NULL|
|`WAREHOUSE_SHIPMENT_DEPART`|`shipments`|Фиксация выезда|`driver_user_id`|NULL|
|`WAREHOUSE_SHIPMENT_ARRIVE`|`shipments`|Фиксация прибытия|`driver_user_id`|NULL|
|`WAREHOUSE_SHIPMENT_CLOSE`|`shipments`|Закрытие рейса|`driver_user_id`|NULL|

---

## **7. Доставка (курьеры)**

|Action|Table Name|Описание|user_id|actor_identifier|
|---|---|---|---|---|
|`COURIER_ROUTE_START`|`delivery_routes`|Старт рейса курьера|`courier_user_id`|NULL|
|`COURIER_GPS_UPDATE`|`geolocation`|Обновление GPS-координат|`courier_user_id`|NULL|
|`COURIER_PAYMENT_COLLECT`|`payments`|Прием оплаты у двери|`courier_user_id`|NULL|
|`COURIER_DELIVERY_COMPLETE`|`waybills`|Завершение доставки|`courier_user_id`|NULL|
|`COURIER_DELIVERY_FAILED`|`waybills`|Неудачная попытка доставки|`courier_user_id`|NULL|
|`COURIER_RECIPIENT_VERIFY`|`waybills`|Верификация получателя курьером|`courier_user_id`|NULL|

---

## **8. HR и кадры**

|Action|Table Name|Описание|user_id|actor_identifier|
|---|---|---|---|---|
|`EMPLOYEE_CREATE`|`employees`|Создание карточки сотрудника|`hr_user_id`|NULL|
|`EMPLOYEE_UPDATE`|`employees`|Изменение данных сотрудника|`hr_user_id`|NULL|
|`EMPLOYEE_POSITION_CHANGE`|`employees`|Изменение должности|`hr_user_id`|NULL|
|`EMPLOYEE_TERMINATE`|`employees`|Увольнение сотрудника|`hr_user_id`|NULL|
|`EMPLOYEE_ROLE_SYNC`|`users`|Синхронизация роли доступа|`hr_user_id`|NULL|

---

## **9. Бухгалтерия**

|Action|Table Name|Описание|user_id|actor_identifier|
|---|---|---|---|---|
|`SALARY_UPDATE`|`salaries`|Изменение оклада/начислений|`accountant_user_id`|NULL|
|`PAYROLL_GENERATE`|`payrolls`|Формирование ведомости|`accountant_user_id`|NULL|
|`PAYOUT_CALCULATE`|`payouts`|Расчет выплат исполнителям|`accountant_user_id`|NULL|
|`PAYOUT_CONFIRM`|`payouts`|Подтверждение выплаты|`accountant_user_id`|NULL|
|`PAYOUT_ADJUSTMENT`|`payouts`|Корректировка/удержание|`accountant_user_id`|NULL|
|`RECONCILIATION_CREATE`|`reconciliations`|Сверка платежей/кассы|`accountant_user_id`|NULL|
|`RECONCILIATION_DISPUTE`|`reconciliations`|Эскалация расхождения|`accountant_user_id`|NULL|

---

## **10. Администрирование системы**

|Action|Table Name|Описание|user_id|actor_identifier|
|---|---|---|---|---|
|`SYSTEM_CONFIG_CHANGE`|`system_config`|Изменение конфигурации|`admin_user_id`|NULL|
|`PERMISSION_GRANT`|`permissions`|Выдача разрешения|`admin_user_id`|NULL|
|`PERMISSION_REVOKE`|`permissions`|Отзыв разрешения|`admin_user_id`|NULL|
|`AUDIT_LOG_VIEW`|`audit_logs`|Просмотр журнала аудита|`admin_user_id`|NULL|
|`SYSTEM_INTEGRATION_TOGGLE`|`integrations`|Вкл/выкл интеграции|`admin_user_id`|NULL|
|`DATA_EXPORT`|`various`|Экспорт данных|`admin_user_id`|NULL|
|`DATA_ARCHIVE`|`various`|Архивация данных|`NULL`|'SYSTEM_SCHEDULER'|

---

## **11. Уведомления и рейтинги**

|Action|Table Name|Описание|user_id|actor_identifier|
|---|---|---|---|---|
|`NOTIFICATION_SUBSCRIBE`|`notification_subscriptions`|Подписка на уведомления|`user_id`|NULL|
|`NOTIFICATION_UNSUBSCRIBE`|`notification_subscriptions`|Отписка от уведомлений|`user_id`|NULL|
|`RATING_SUBMIT`|`ratings`|Оставление оценки/отзыва|`user_id`|NULL|

---

## **12. Трекинг и геолокация**

|Action|Table Name|Описание|user_id|actor_identifier|
|---|---|---|---|---|
|`TRACKING_VIEW_PUBLIC`|`waybills`|Просмотр трекинга (публично)|`NULL`|phone_number|
|`TRACKING_ETA_UPDATE`|`tracking`|Обновление прогноза ETA|`dispatcher_user_id`|NULL|
|`GEOLOCATION_UPDATE`|`geolocation`|Передача GPS-координат|`courier_user_id` или `driver_user_id`|NULL|

---

## Итого: **~75 типов audit-логов**

Этот список покрывает все критически важные действия пользователей в вашей логистической системе, обеспечивая полную прослеживаемость операций для безопасности, расследований и отчетности.

1. [https://ppl-ai-file-upload.s3.amazonaws.com/web/direct-files/collection_bc53515c-d239-4615-bc76-b22d885ea048/ba33ce5b-fa4c-4d0a-980b-2e7dd3c24a7e/Roli-polzovatelei.md](https://ppl-ai-file-upload.s3.amazonaws.com/web/direct-files/collection_bc53515c-d239-4615-bc76-b22d885ea048/ba33ce5b-fa4c-4d0a-980b-2e7dd3c24a7e/Roli-polzovatelei.md)
2. [https://ppl-ai-file-upload.s3.amazonaws.com/web/direct-files/collection_bc53515c-d239-4615-bc76-b22d885ea048/d363031c-0610-42d3-a694-6a04bc6177a8/Tablitsa-REST-API.md](https://ppl-ai-file-upload.s3.amazonaws.com/web/direct-files/collection_bc53515c-d239-4615-bc76-b22d885ea048/d363031c-0610-42d3-a694-6a04bc6177a8/Tablitsa-REST-API.md)
3. [https://ppl-ai-file-upload.s3.amazonaws.com/web/direct-files/collection_bc53515c-d239-4615-bc76-b22d885ea048/9c076add-2d49-4c85-a39f-9cdfaf9ca358/Stsenarii-raboty.md](https://ppl-ai-file-upload.s3.amazonaws.com/web/direct-files/collection_bc53515c-d239-4615-bc76-b22d885ea048/9c076add-2d49-4c85-a39f-9cdfaf9ca358/Stsenarii-raboty.md)