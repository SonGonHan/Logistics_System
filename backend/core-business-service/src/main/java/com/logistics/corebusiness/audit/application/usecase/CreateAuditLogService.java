package com.logistics.corebusiness.audit.application.usecase;

import com.logistics.corebusiness.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.corebusiness.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.corebusiness.audit.application.port.out.AuditLogRepository;
import com.logistics.corebusiness.audit.domain.AuditLog;
import com.logistics.shared.audit_action.AuditActionTypeService;
import com.logistics.shared.audit_action.domain.AuditActionType;
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
 * Сервис для создания записей в журнале аудита core-business-service.
 *
 * <h2>Назначение</h2>
 * Централизованное создание audit logs для всех операций:
 * - Черновики (DRAFT_CREATE, DRAFT_UPDATE, DRAFT_CANCEL)
 * - Приёмка на ПВЗ (PVZ_ACCEPTANCE_START, PVZ_WEIGHT_VERIFY, PVZ_PHOTO_UPLOAD)
 * - Накладные (WAYBILL_FINALIZE, WAYBILL_CREATE, WAYBILL_STATUS_CHANGE, WAYBILL_CANCEL)
 * - Доп. услуги (WAYBILL_SERVICE_ADD)
 * - Рейтинг (RATING_SUBMIT)
 *
 * <h2>Особенности</h2>
 * - Кэширует AuditActionType при старте для производительности
 * - userId хранится как Long (этот сервис не владеет таблицей users)
 * - Конвертирует IP string в PostgreSQL INET тип
 * - Не бросает исключения (ошибки аудита не должны ломать бизнес-логику)
 *
 * @implements CreateAuditLogUseCase
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateAuditLogService implements CreateAuditLogUseCase {

    private final AuditLogRepository auditLogRepository;
    private final AuditActionTypeService auditActionTypeService;

    private final Map<String, AuditActionType> actionTypeCache = new ConcurrentHashMap<>();

    /**
     * Инициализирует кэш типов действий при старте приложения.
     * Загружает все action types, используемые в CoreBusinessService.
     *
     * @throws IllegalStateException если какой-то action type не найден в БД
     */
    @PostConstruct
    public void initActionTypeCache() {
        log.info("Инициализация кэша типов аудит-действий для core-business-service");

        var requiredActionTypes = List.of(
                "DRAFT_CREATE",
                "DRAFT_UPDATE",
                "DRAFT_CANCEL",
                "WAYBILL_FINALIZE",
                "WAYBILL_STATUS_CHANGE",
                "WAYBILL_CANCEL",
                "WAYBILL_SERVICE_ADD",
                "PVZ_ACCEPTANCE_START",
                "PVZ_WEIGHT_VERIFY",
                "PVZ_PHOTO_UPLOAD",
                "RATING_SUBMIT",
                "WAYBILL_CREATE"
        );

        for (var actionName : requiredActionTypes) {
            auditActionTypeService.getActionTypeActionName(actionName)
                    .ifPresentOrElse(
                            actionType -> {
                                actionTypeCache.put(actionName, actionType);
                                log.debug("Загружен action type: {} (id={})", actionName, actionType.getId());
                            },
                            () -> {
                                var message = String.format("Action type '%s' не найден в БД.", actionName);
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

            var actionType = actionTypeCache.get(command.actionTypeName());
            if (actionType == null) {
                log.error("Action type '{}' не найден в кэше", command.actionTypeName());
                return;
            }

            Inet ipAddress = null;
            if (command.ipAddress() != null && !command.ipAddress().isBlank()) {
                try {
                    ipAddress = new Inet(command.ipAddress());
                } catch (Exception e) {
                    log.warn("Не удалось преобразовать IP '{}' в Inet: {}",
                            command.ipAddress(), e.getMessage());
                }
            }

            var auditLog = AuditLog.builder()
                    .userId(command.userId())
                    .actionType(actionType)
                    .actorIdentifier(command.actorIdentifier())
                    .ipAddress(ipAddress)
                    .newValues(command.newValues())
                    .tableName(command.tableName())
                    .recordId(command.recordId() != null ? command.recordId() : 0)
                    .performedAt(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);

            log.debug("Audit log успешно создан: actionType={}, userId={}",
                    command.actionTypeName(), command.userId());

        } catch (Exception e) {
            log.error("Ошибка при создании audit log для action='{}', userId={}: {}",
                    command.actionTypeName(), command.userId(), e.getMessage(), e);
        }
    }
}
