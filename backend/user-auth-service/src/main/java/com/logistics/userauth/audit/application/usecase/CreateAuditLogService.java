package com.logistics.userauth.audit.application.usecase;

import com.logistics.shared.audit_action.AuditActionTypeService;
import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.userauth.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.userauth.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.userauth.audit.application.port.out.AuditLogRepository;
import com.logistics.userauth.audit.domain.AuditLog;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис для создания записей в журнале аудита.
 *
 * <h2>Назначение</h2>
 * Централизованное создание audit logs для всех операций в системе:
 * - Аутентификация (USER_REGISTER, USER_LOGIN_SUCCESS, USER_LOGIN_FAILURE, USER_LOGOUT)
 * - Управление сессиями (SESSION_CREATE, SESSION_REVOKE, TOKEN_REFRESH)
 * - Управление пользователями (USER_UPDATE, PASSWORD_CHANGE)
 *
 * <h2>Особенности</h2>
 * - Кэширует AuditActionType при старте для производительности
 * - Обрабатывает null userId (для неудачных попыток входа)
 * - Конвертирует IP string в PostgreSQL INET тип
 * - Не бросает исключения (ошибки аудита не должны ломать бизнес-логику)
 *
 * <h2>Безопасность</h2>
 * - НИКОГДА не логирует пароли (plain или hashed)
 * - НИКОГДА не логирует токены доступа
 * - IP адреса и User-Agent логируются для security audit
 *
 * @implements CreateAuditLogUseCase
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateAuditLogService implements CreateAuditLogUseCase {

    private final AuditLogRepository auditLogRepository;
    private final AuditActionTypeService auditActionTypeService;
    private final UserRepository userRepository;
    
    private final Map<String, AuditActionType> actionTypeCache = new ConcurrentHashMap<>();

    /**
     * Инициализирует кэш типов действий при старте приложения.
     * Загружает все action types, используемые в UserAuthService.
     *
     * @throws IllegalStateException если какой-то action type не найден в БД
     */
    @PostConstruct
    public void initActionTypeCache() {
        log.info("Инициализация кэша типов аудит-действий");

        var requiredActionTypes = List.of(
                "USER_REGISTER",
                "USER_LOGIN_SUCCESS",
                "USER_LOGIN_FAILURE",
                "USER_LOGOUT",
                "PASSWORD_CHANGE",
                "SESSION_CREATE",
                "SESSION_REVOKE",
                "TOKEN_REFRESH",
                "USER_UPDATE"
        );

        for (var actionName : requiredActionTypes) {
            auditActionTypeService.getActionTypeActionName(actionName)
                    .ifPresentOrElse(
                            actionType -> {
                                actionTypeCache.put(actionName, actionType);
                                log.debug("Загружен action type: {} (id={})", actionName, actionType.getId());
                            },
                            () -> {
                                var message = String.format(
                                        "Action type '%s' не найден в БД. " +
                                                "Проверьте миграцию V3__insert_audit_action_types.sql",
                                        actionName
                                );
                                log.error(message);
                                throw new IllegalStateException(message);
                            }
                    );
        }

        log.info("Кэш типов аудит-действий инициализирован: {} типов", actionTypeCache.size());
    }

    /**
     * Создаёт запись в журнале аудита.
     *
     * <p>Алгоритм:</p>
     * <ul>
     *   <li>Получает AuditActionType из кэша</li>
     *   <li>Загружает User из БД (если userId не null)</li>
     *   <li>Конвертирует IP string в Inet</li>
     *   <li>Создаёт доменный объект AuditLog</li>
     *   <li>Сохраняет через repository</li>
     * </ul>
     *
     * <p>Обработка ошибок:</p>
     * - Ошибки аудита логируются но НЕ бросают исключения
     * - Это гарантирует что проблемы с аудитом не сломают бизнес-операции
     *
     * @param command Команда с данными для аудит лога
     */
    @Override
    public void create(CreateAuditLogCommand command) {
        try {
            log.debug("Создание audit log: actionType={}, userId={}, actor={}",
                    command.actionTypeName(), command.userId(), command.actorIdentifier());

            // 1. Получаем action type из кэша
            var actionType = actionTypeCache.get(command.actionTypeName());
            if (actionType == null) {
                log.error("Action type '{}' не найден в кэше", command.actionTypeName());
                return;
            }

            // 2. Загружаем пользователя (если известен)
            User user = null;
            if (command.userId() != null) {
                user = userRepository.findById(command.userId()).orElse(null);
                if (user == null) {
                    log.warn("Пользователь с id={} не найден для audit log", command.userId());
                }
            }

            // 3. Конвертируем IP в Inet
            Inet ipAddress = null;
            if (command.ipAddress() != null && !command.ipAddress().isBlank()) {
                try {
                    ipAddress = new Inet(command.ipAddress());
                } catch (Exception e) {
                    log.warn("Не удалось преобразовать IP '{}' в Inet: {}",
                            command.ipAddress(), e.getMessage());
                }
            }

            // 4. Создаём доменный объект
            var auditLog = AuditLog.builder()
                    .user(user)
                    .actionType(actionType)
                    .actorIdentifier(command.actorIdentifier())
                    .ipAddress(ipAddress)
                    .newValues(command.newValues())
                    .tableName(command.tableName())
                    .recordId(command.recordId() != null ? command.recordId() : 0)
                    .performedAt(LocalDateTime.now())
                    .build();

            // 5. Сохраняем
            auditLogRepository.save(auditLog);

            log.debug("Audit log успешно создан: actionType={}, userId={}",
                    command.actionTypeName(), command.userId());

        } catch (Exception e) {
            // КРИТИЧНО: Не бросаем исключение наружу!
            // Ошибки аудита не должны ломать бизнес-операции
            log.error("Ошибка при создании audit log для action='{}', userId={}: {}",
                    command.actionTypeName(), command.userId(), e.getMessage(), e);
        }
    }
}