package com.logistics.userauth.audit.application.port.in;

import com.logistics.userauth.audit.application.port.in.command.CreateAuditLogCommand;

/**
 * Use Case для создания записи в журнале аудита.
 *
 * <h2>Назначение</h2>
 * Определяет контракт для логирования действий пользователей в системе:
 * - Аутентификация (вход, выход, обновление токена)
 * - Управление пользователями (изменение профиля, смена пароля)
 * - Управление сессиями (создание, отзыв)
 *
 * <h2>Имплементация</h2>
 * @see com.logistics.userauth.audit.application.usecase.CreateAuditLogService
 *
 * <h2>Использование</h2>
 * <pre>
 * &#64;Service
 * public class RegisterUserService {
 *     private final CreateAuditLogUseCase createAuditLogUseCase;
 *
 *     public void register(RegisterUserCommand command) {
 *         // ... бизнес-логика регистрации
 *
 *         // Логируем действие
 *         createAuditLogUseCase.create(new CreateAuditLogCommand(
 *             savedUser.getId(),
 *             "USER_REGISTER",
 *             savedUser.getPhone(),
 *             command.ipAddress(),
 *             command.userAgent(),
 *             Map.of("email", savedUser.getEmail(), "phone", savedUser.getPhone()),
 *             "users",
 *             savedUser.getId()
 *         ));
 *     }
 * }
 * </pre>
 */
public interface CreateAuditLogUseCase {

    /**
     * Создаёт запись в журнале аудита.
     *
     * @param command Команда с данными для аудит лога
     * @throws IllegalArgumentException если actionTypeName не найден в БД
     */
    void create(CreateAuditLogCommand command);
}