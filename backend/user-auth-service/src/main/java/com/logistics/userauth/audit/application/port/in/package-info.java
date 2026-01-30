/**
 * Входные порты (Input Ports) для use cases модуля аудита.
 *
 * <h2>Содержание</h2>
 * Интерфейсы use case для работы с журналом аудита:
 * - {@link com.logistics.userauth.audit.application.port.in.CreateAuditLogUseCase}: Создание audit log
 *
 * <h2>Паттерн</h2>
 * Следует принципам Clean Architecture / Hexagonal Architecture:
 * - Входные порты определяют что может делать приложение (use cases)
 * - Адаптеры (контроллеры, сервисы) зависят от этих портов
 * - Имплементации находятся в {@link com.logistics.userauth.audit.application.usecase}
 *
 * @see com.logistics.userauth.audit.application.usecase
 */
package com.logistics.userauth.audit.application.port.in;