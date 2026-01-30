/**
 * Имплементации use cases модуля аудита.
 *
 * <h2>Содержание</h2>
 * Сервисы, реализующие бизнес-логику аудит логирования:
 * - {@link com.logistics.userauth.audit.application.usecase.CreateAuditLogService}: Создание audit logs
 *
 * <h2>Ответственность</h2>
 * Use case сервисы:
 * - Оркеструют выполнение бизнес-операций
 * - Используют output ports (repositories, external services)
 * - Применяют доменную логику
 * - Обрабатывают ошибки
 *
 * <h2>Особенности аудит логирования</h2>
 * - Кэширование AuditActionType для производительности
 * - Обработка null userId (для неудачных попыток входа)
 * - Конвертация IP string в PostgreSQL INET тип
 * - Ошибки аудита НЕ должны ломать бизнес-операции (try-catch)
 *
 * @see com.logistics.userauth.audit.application.port.in
 * @see com.logistics.userauth.audit.application.port.out
 */
package com.logistics.userauth.audit.application.usecase;