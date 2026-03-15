package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.corebusiness.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.corebusiness.waybill.adapter.in.WaybillControllerMapper;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.WaybillResponse;
import com.logistics.corebusiness.waybill.application.exception.WaybillInvalidStatusTransitionException;
import com.logistics.corebusiness.waybill.application.exception.WaybillNotFoundException;
import com.logistics.corebusiness.waybill.application.port.in.UpdateWaybillStatusUseCase;
import com.logistics.corebusiness.waybill.application.port.in.command.UpdateWaybillStatusCommand;
import com.logistics.corebusiness.waybill.application.port.out.WaybillRepository;
import com.logistics.corebusiness.waybill.application.port.out.WaybillStatusHistoryRepository;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import com.logistics.corebusiness.waybill.domain.WaybillStatusHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * Сервис изменения статуса подтвержденной накладной.
 *
 * <h2>Назначение</h2>
 * Проверяет допустимость перехода статуса и фиксирует историю изменений.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateWaybillStatusService implements UpdateWaybillStatusUseCase {

    private static final Map<WaybillStatus, Set<WaybillStatus>> VALID_TRANSITIONS = Map.of(
            WaybillStatus.ACCEPTED_AT_PVZ, Set.of(
                    WaybillStatus.IN_TRANSIT,
                    WaybillStatus.CANCELLED
            ),
            WaybillStatus.IN_TRANSIT, Set.of(
                    WaybillStatus.AT_SORTING_CENTER,
                    WaybillStatus.OUT_FOR_DELIVERY,
                    WaybillStatus.RETURNING
            ),
            WaybillStatus.AT_SORTING_CENTER, Set.of(
                    WaybillStatus.IN_TRANSIT,
                    WaybillStatus.OUT_FOR_DELIVERY
            ),
            WaybillStatus.OUT_FOR_DELIVERY, Set.of(
                    WaybillStatus.READY_FOR_PICKUP,
                    WaybillStatus.DELIVERED,
                    WaybillStatus.RETURNING
            ),
            WaybillStatus.READY_FOR_PICKUP, Set.of(
                    WaybillStatus.DELIVERED,
                    WaybillStatus.RETURNING
            )
    );

    private final WaybillRepository waybillRepository;
    private final WaybillStatusHistoryRepository historyRepository;
    private final CreateAuditLogUseCase auditLogUseCase;

    @Override
    public WaybillResponse updateStatus(UpdateWaybillStatusCommand command) {
        var waybill = waybillRepository.findById(command.waybillId())
                .orElseThrow(() -> WaybillNotFoundException.byId(command.waybillId()));

        var currentStatus = waybill.getStatus();
        var allowedStatuses = VALID_TRANSITIONS.get(currentStatus);
        if (allowedStatuses == null) {
            throw WaybillInvalidStatusTransitionException.terminalStatus(currentStatus);
        }
        if (!allowedStatuses.contains(command.newStatus())) {
            throw WaybillInvalidStatusTransitionException.invalidTransition(currentStatus, command.newStatus());
        }

        waybill.setStatus(command.newStatus());
        var savedWaybill = waybillRepository.save(waybill);
        var now = LocalDateTime.now();

        var historyEntry = WaybillStatusHistory.builder()
                .waybillId(savedWaybill.getId())
                .status(command.newStatus())
                .facilityId(command.facilityId())
                .notes(command.notes())
                .changedBy(command.operatorId())
                .changedAt(now)
                .build();
        historyRepository.save(historyEntry);

        Map<String, Object> details = Map.of(
                "fromStatus", currentStatus.name(),
                "toStatus", command.newStatus().name()
        );
        auditLogUseCase.create(new CreateAuditLogCommand(
                command.operatorId(),
                "WAYBILL_STATUS_CHANGE",
                null,
                null,
                details,
                "waybills",
                savedWaybill.getId()
        ));

        if (command.newStatus() == WaybillStatus.CANCELLED) {
            auditLogUseCase.create(new CreateAuditLogCommand(
                    command.operatorId(),
                    "WAYBILL_CANCEL",
                    null,
                    null,
                    details,
                    "waybills",
                    savedWaybill.getId()
            ));
        }

        return WaybillControllerMapper.toResponse(savedWaybill);
    }
}
