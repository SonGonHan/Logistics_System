package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.corebusiness.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.corebusiness.waybill.application.exception.WaybillInvalidStatusTransitionException;
import com.logistics.corebusiness.waybill.application.exception.WaybillNotFoundException;
import com.logistics.corebusiness.waybill.application.port.in.command.UpdateWaybillStatusCommand;
import com.logistics.corebusiness.waybill.application.port.out.WaybillRepository;
import com.logistics.corebusiness.waybill.application.port.out.WaybillStatusHistoryRepository;
import com.logistics.corebusiness.waybill.domain.Waybill;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import com.logistics.corebusiness.waybill.domain.WaybillStatusHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateWaybillStatusService - тестирование изменения статуса накладной")
class UpdateWaybillStatusServiceTest {

    @Mock
    private WaybillRepository waybillRepository;

    @Mock
    private WaybillStatusHistoryRepository historyRepository;

    @Mock
    private CreateAuditLogUseCase auditLogUseCase;

    @InjectMocks
    private UpdateWaybillStatusService service;

    @Captor
    private ArgumentCaptor<WaybillStatusHistory> historyCaptor;

    @Captor
    private ArgumentCaptor<CreateAuditLogCommand> auditCaptor;

    @Test
    @DisplayName("Должен успешно обновить статус ACCEPTED_AT_PVZ -> IN_TRANSIT")
    void shouldUpdateStatusSuccessfully() {
        var waybill = createWaybill(WaybillStatus.ACCEPTED_AT_PVZ);
        when(waybillRepository.findById(1L)).thenReturn(Optional.of(waybill));
        when(waybillRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.updateStatus(UpdateWaybillStatusCommand.builder()
                .waybillId(1L)
                .newStatus(WaybillStatus.IN_TRANSIT)
                .operatorId(50L)
                .facilityId(7L)
                .notes("Отправлено")
                .build());

        assertThat(response.status()).isEqualTo(WaybillStatus.IN_TRANSIT);
    }

    @Test
    @DisplayName("Должен создать запись в истории статусов")
    void shouldCreateStatusHistoryEntry() {
        var waybill = createWaybill(WaybillStatus.ACCEPTED_AT_PVZ);
        when(waybillRepository.findById(1L)).thenReturn(Optional.of(waybill));
        when(waybillRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateStatus(UpdateWaybillStatusCommand.builder()
                .waybillId(1L)
                .newStatus(WaybillStatus.IN_TRANSIT)
                .operatorId(50L)
                .facilityId(7L)
                .notes("Отправлено")
                .build());

        verify(historyRepository).save(historyCaptor.capture());
        assertThat(historyCaptor.getValue().getStatus()).isEqualTo(WaybillStatus.IN_TRANSIT);
        assertThat(historyCaptor.getValue().getFacilityId()).isEqualTo(7L);
        assertThat(historyCaptor.getValue().getChangedBy()).isEqualTo(50L);
    }

    @Test
    @DisplayName("Должен записать audit при изменении статуса")
    void shouldAuditStatusChange() {
        var waybill = createWaybill(WaybillStatus.ACCEPTED_AT_PVZ);
        when(waybillRepository.findById(1L)).thenReturn(Optional.of(waybill));
        when(waybillRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateStatus(UpdateWaybillStatusCommand.builder()
                .waybillId(1L)
                .newStatus(WaybillStatus.IN_TRANSIT)
                .operatorId(50L)
                .build());

        verify(auditLogUseCase).create(auditCaptor.capture());
        assertThat(auditCaptor.getValue().actionTypeName()).isEqualTo("WAYBILL_STATUS_CHANGE");
        assertThat(auditCaptor.getValue().newValues()).containsEntry("fromStatus", "ACCEPTED_AT_PVZ");
        assertThat(auditCaptor.getValue().newValues()).containsEntry("toStatus", "IN_TRANSIT");
    }

    @Test
    @DisplayName("Должен отдельно записать audit для отмены накладной")
    void shouldAuditCancellationSeparately() {
        var waybill = createWaybill(WaybillStatus.ACCEPTED_AT_PVZ);
        when(waybillRepository.findById(1L)).thenReturn(Optional.of(waybill));
        when(waybillRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateStatus(UpdateWaybillStatusCommand.builder()
                .waybillId(1L)
                .newStatus(WaybillStatus.CANCELLED)
                .operatorId(50L)
                .build());

        verify(auditLogUseCase, times(2)).create(auditCaptor.capture());
        assertThat(auditCaptor.getAllValues()).extracting(CreateAuditLogCommand::actionTypeName)
                .containsExactly("WAYBILL_STATUS_CHANGE", "WAYBILL_CANCEL");
    }

    @Test
    @DisplayName("Должен выбросить исключение при недопустимом переходе статуса")
    void shouldThrowOnInvalidTransition() {
        when(waybillRepository.findById(1L)).thenReturn(Optional.of(createWaybill(WaybillStatus.ACCEPTED_AT_PVZ)));

        assertThatThrownBy(() -> service.updateStatus(UpdateWaybillStatusCommand.builder()
                .waybillId(1L)
                .newStatus(WaybillStatus.DELIVERED)
                .operatorId(50L)
                .build()))
                .isInstanceOf(WaybillInvalidStatusTransitionException.class)
                .hasMessage("Invalid status transition from ACCEPTED_AT_PVZ to DELIVERED");
    }

    @Test
    @DisplayName("Должен выбросить исключение для терминального статуса")
    void shouldThrowOnTerminalStatus() {
        when(waybillRepository.findById(1L)).thenReturn(Optional.of(createWaybill(WaybillStatus.DELIVERED)));

        assertThatThrownBy(() -> service.updateStatus(UpdateWaybillStatusCommand.builder()
                .waybillId(1L)
                .newStatus(WaybillStatus.RETURNED)
                .operatorId(50L)
                .build()))
                .isInstanceOf(WaybillInvalidStatusTransitionException.class)
                .hasMessage("Waybill is in terminal status: DELIVERED. No further transitions allowed.");
    }

    @Test
    @DisplayName("Должен выбросить исключение когда накладная не найдена")
    void shouldThrowWhenWaybillNotFound() {
        when(waybillRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus(UpdateWaybillStatusCommand.builder()
                .waybillId(999L)
                .newStatus(WaybillStatus.IN_TRANSIT)
                .operatorId(50L)
                .build()))
                .isInstanceOf(WaybillNotFoundException.class)
                .hasMessage("Waybill not found with id: 999");
    }

    private Waybill createWaybill(WaybillStatus status) {
        return Waybill.builder()
                .id(1L)
                .waybillNumber("WB-260315-123456")
                .waybillCreatorId(10L)
                .senderUserId(20L)
                .recipientUserId(30L)
                .recipientAddress("Адрес")
                .pricingRuleId(40L)
                .finalPrice(new BigDecimal("750.00"))
                .status(status)
                .createdAt(LocalDateTime.now())
                .acceptedAt(LocalDateTime.now())
                .build();
    }
}
