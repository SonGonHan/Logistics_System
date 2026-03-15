package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.corebusiness.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.corebusiness.waybill.application.exception.DraftInvalidStatusException;
import com.logistics.corebusiness.waybill.application.exception.DraftNotFoundException;
import com.logistics.corebusiness.waybill.application.exception.WaybillValidationException;
import com.logistics.corebusiness.waybill.application.port.in.command.CreateWaybillCommand;
import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.application.port.out.WaybillRepository;
import com.logistics.corebusiness.waybill.application.port.out.WaybillStatusHistoryRepository;
import com.logistics.corebusiness.waybill.application.util.WaybillNumberGenerator;
import com.logistics.corebusiness.waybill.domain.Draft;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import com.logistics.corebusiness.waybill.domain.Waybill;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import com.logistics.corebusiness.waybill.domain.WaybillStatusHistory;
import com.logistics.shared.pricing_rule.PricingRuleService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateWaybillService - тестирование создания накладной")
class CreateWaybillServiceTest {

    @Mock
    private WaybillRepository waybillRepository;

    @Mock
    private DraftRepository draftRepository;

    @Mock
    private WaybillStatusHistoryRepository historyRepository;

    @Mock
    private WaybillNumberGenerator waybillNumberGenerator;

    @Mock
    private PricingRuleService pricingRuleService;

    @Mock
    private CreateAuditLogUseCase auditLogUseCase;

    @InjectMocks
    private CreateWaybillService service;

    @Captor
    private ArgumentCaptor<Waybill> waybillCaptor;

    @Captor
    private ArgumentCaptor<WaybillStatusHistory> historyCaptor;

    @Captor
    private ArgumentCaptor<Draft> draftCaptor;

    @Captor
    private ArgumentCaptor<CreateAuditLogCommand> auditCaptor;

    @Test
    @DisplayName("Должен успешно создать накладную из черновика")
    void shouldCreateWaybillFromDraftSuccessfully() {
        var draft = Draft.builder()
                .id(10L)
                .draftCreatorId(1L)
                .senderUserId(1L)
                .recipientUserId(2L)
                .recipientAddress("Москва, ул. Тестовая, д. 1")
                .pricingRuleId(3L)
                .draftStatus(DraftStatus.PENDING)
                .build();

        when(waybillNumberGenerator.generate()).thenReturn("WB-260315-123456");
        when(draftRepository.findById(10L)).thenReturn(java.util.Optional.of(draft));
        when(pricingRuleService.calculatePrice(3L)).thenReturn(new BigDecimal("500.00"));
        when(waybillRepository.save(any())).thenAnswer(invocation -> {
            var waybill = invocation.getArgument(0, Waybill.class);
            waybill.setId(100L);
            return waybill;
        });

        var command = CreateWaybillCommand.builder()
                .waybillCreatorId(50L)
                .draftId(10L)
                .build();

        var response = service.create(command);

        verify(waybillRepository).save(waybillCaptor.capture());
        verify(historyRepository).save(historyCaptor.capture());
        verify(draftRepository).save(draftCaptor.capture());
        verify(auditLogUseCase).create(auditCaptor.capture());

        var savedWaybill = waybillCaptor.getValue();
        assertThat(savedWaybill.getWaybillNumber()).isEqualTo("WB-260315-123456");
        assertThat(savedWaybill.getSenderUserId()).isEqualTo(1L);
        assertThat(savedWaybill.getRecipientUserId()).isEqualTo(2L);
        assertThat(savedWaybill.getFinalPrice()).isEqualByComparingTo("500.00");
        assertThat(savedWaybill.getStatus()).isEqualTo(WaybillStatus.ACCEPTED_AT_PVZ);

        assertThat(draftCaptor.getValue().getDraftStatus()).isEqualTo(DraftStatus.CONFIRMED);
        assertThat(historyCaptor.getValue().getStatus()).isEqualTo(WaybillStatus.ACCEPTED_AT_PVZ);
        assertThat(auditCaptor.getValue().actionTypeName()).isEqualTo("WAYBILL_FINALIZE");
        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.status()).isEqualTo(WaybillStatus.ACCEPTED_AT_PVZ);
    }

    @Test
    @DisplayName("Должен успешно создать накладную без черновика")
    void shouldCreateWaybillWithoutDraftSuccessfully() {
        when(waybillNumberGenerator.generate()).thenReturn("WB-260315-654321");
        when(pricingRuleService.calculatePrice(7L)).thenReturn(new BigDecimal("900.00"));
        when(waybillRepository.save(any())).thenAnswer(invocation -> {
            var waybill = invocation.getArgument(0, Waybill.class);
            waybill.setId(101L);
            return waybill;
        });

        var command = CreateWaybillCommand.builder()
                .waybillCreatorId(60L)
                .senderUserId(60L)
                .recipientUserId(77L)
                .recipientAddress("Казань, ул. Баумана, д. 3")
                .pricingRuleId(7L)
                .build();

        var response = service.create(command);

        verify(auditLogUseCase).create(auditCaptor.capture());
        assertThat(auditCaptor.getValue().actionTypeName()).isEqualTo("WAYBILL_CREATE");
        assertThat(response.id()).isEqualTo(101L);
        assertThat(response.finalPrice()).isEqualByComparingTo("900.00");
    }

    @Test
    @DisplayName("Должен выбросить исключение когда черновик не найден")
    void shouldThrowWhenDraftNotFound() {
        when(waybillNumberGenerator.generate()).thenReturn("WB-260315-111111");
        when(draftRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        var command = CreateWaybillCommand.builder()
                .waybillCreatorId(1L)
                .draftId(999L)
                .build();

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(DraftNotFoundException.class)
                .hasMessage("Draft not found with id: 999");
    }

    @Test
    @DisplayName("Должен выбросить исключение когда черновик не в статусе PENDING")
    void shouldThrowWhenDraftNotPending() {
        var draft = Draft.builder()
                .id(15L)
                .draftStatus(DraftStatus.CONFIRMED)
                .build();

        when(waybillNumberGenerator.generate()).thenReturn("WB-260315-222222");
        when(draftRepository.findById(15L)).thenReturn(java.util.Optional.of(draft));

        var command = CreateWaybillCommand.builder()
                .waybillCreatorId(1L)
                .draftId(15L)
                .build();

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(DraftInvalidStatusException.class);
    }

    @Test
    @DisplayName("Должен выбросить исключение при отсутствии обязательных полей без черновика")
    void shouldThrowWhenRequiredFieldsMissingWithoutDraft() {
        when(waybillNumberGenerator.generate()).thenReturn("WB-260315-333333");

        var command = CreateWaybillCommand.builder()
                .waybillCreatorId(1L)
                .senderUserId(1L)
                .pricingRuleId(2L)
                .build();

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(WaybillValidationException.class);

        verify(waybillRepository, never()).save(any());
    }
}
