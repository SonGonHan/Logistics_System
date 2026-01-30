/**
 * Команды (Commands) для входных портов модуля аудита.
 *
 * <h2>Содержание</h2>
 * Command объекты для передачи данных в use cases:
 * - {@link com.logistics.userauth.audit.application.port.in.command.CreateAuditLogCommand}: Данные для создания audit log
 *
 * <h2>Паттерн Command</h2>
 * Commands инкапсулируют все параметры use case в один объект:
 * - Типобезопасность (type-safe parameters)
 * - Удобство для тестирования
 * - Возможность валидации
 * - Иммутабельность (record types в Java)
 *
 * <h2>Использование</h2>
 * <pre>
 * var command = new CreateAuditLogCommand(
 *     userId,
 *     "USER_LOGIN_SUCCESS",
 *     user.getPhone(),
 *     request.getRemoteAddr(),
 *     request.getHeader("User-Agent"),
 *     Map.of("sessionId", session.getId()),
 *     null,
 *     null
 * );
 * createAuditLogUseCase.create(command);
 * </pre>
 */
package com.logistics.userauth.audit.application.port.in.command;