package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.corebusiness.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.DraftResponse;
import com.logistics.corebusiness.waybill.application.exception.DraftNotFoundException;
import com.logistics.corebusiness.waybill.application.port.in.command.UpdateDraftCommand;
import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.domain.Draft;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import com.logistics.shared.pricing_rule.PricingRuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateDraftService - тестирование обновления черновика накладной")
class UpdateDraftServiceTest {

    @Mock
    private DraftRepository repository;

    @Mock
    private PricingRuleService pricingRuleService;

    @Mock
    private CreateAuditLogUseCase auditLogUseCase;

    @Captor
    private ArgumentCaptor<Draft> draftCaptor;

    @Captor
    private ArgumentCaptor<CreateAuditLogCommand> auditCaptor;

    private UpdateDraftService updateDraftService;

    @BeforeEach
    void setUp() {
        updateDraftService = new UpdateDraftService(repository, pricingRuleService, auditLogUseCase);
    }

    @Test
    @DisplayName("Должен успешно обновить адрес и получателя без изменения цены")
    void shouldUpdateAddressAndRecipientWithoutRecalculation() {
        // Given
        Long draftId = 1L;
        Long userId = 100L;

        var existingDraft = Draft.builder()
                .id(draftId)
                .barcode("DRF-260209-123456")
                .draftCreatorId(userId)
                .senderUserId(userId)
                .recipientUserId(200L)
                .recipientAddress("Старый адрес")
                .pricingRuleId(1L)
                .estimatedPrice(new BigDecimal("350.00"))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findById(draftId)).thenReturn(Optional.of(existingDraft));
        when(repository.save(any(Draft.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var command = UpdateDraftCommand.builder()
                .draftId(draftId)
                .userId(userId)
                .recipientUserId(300L)
                .recipientAddress("Новый адрес")
                .pricingRuleId(null)
                .build();

        // When
        DraftResponse result = updateDraftService.update(command);

        // Then
        verify(repository).findById(draftId);
        verify(repository).save(draftCaptor.capture());
        verify(pricingRuleService, never()).calculatePrice(any());

        Draft updatedDraft = draftCaptor.getValue();
        assertThat(updatedDraft.getRecipientUserId()).isEqualTo(300L);
        assertThat(updatedDraft.getRecipientAddress()).isEqualTo("Новый адрес");
        assertThat(updatedDraft.getEstimatedPrice()).isEqualByComparingTo("350.00");

        assertThat(result).isNotNull();
        assertThat(result.recipientAddress()).isEqualTo("Новый адрес");
    }

    @Test
    @DisplayName("Должен пересчитать цену при смене тарифного плана")
    void shouldRecalculatePriceWhenPricingRuleChanges() {
        // Given
        Long draftId = 2L;
        Long userId = 100L;
        Long newRuleId = 3L;

        var existingDraft = Draft.builder()
                .id(draftId)
                .barcode("DRF-260209-222222")
                .draftCreatorId(userId)
                .senderUserId(userId)
                .recipientUserId(200L)
                .recipientAddress("Адрес")
                .pricingRuleId(1L)
                .estimatedPrice(new BigDecimal("350.00"))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findById(draftId)).thenReturn(Optional.of(existingDraft));
        when(repository.save(any(Draft.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pricingRuleService.calculatePrice(newRuleId)).thenReturn(new BigDecimal("650.00"));

        var command = UpdateDraftCommand.builder()
                .draftId(draftId)
                .userId(userId)
                .pricingRuleId(newRuleId)
                .build();

        // When
        DraftResponse result = updateDraftService.update(command);

        // Then
        verify(pricingRuleService).calculatePrice(newRuleId);
        verify(repository).save(draftCaptor.capture());

        Draft updatedDraft = draftCaptor.getValue();
        assertThat(updatedDraft.getPricingRuleId()).isEqualTo(newRuleId);
        assertThat(updatedDraft.getEstimatedPrice()).isEqualByComparingTo("650.00");
    }

    @Test
    @DisplayName("Должен выбросить исключение, если черновик не найден")
    void shouldThrowExceptionWhenDraftNotFound() {
        // Given
        Long draftId = 999L;
        Long userId = 100L;

        when(repository.findById(draftId)).thenReturn(Optional.empty());

        var command = UpdateDraftCommand.builder()
                .draftId(draftId)
                .userId(userId)
                .recipientAddress("Новый адрес")
                .build();

        // When / Then
        assertThatThrownBy(() -> updateDraftService.update(command))
                .isInstanceOf(DraftNotFoundException.class)
                .hasMessageContaining("Draft not found with id: 999");

        verify(repository).findById(draftId);
        verify(repository, never()).save(any());
        verify(auditLogUseCase, never()).create(any());
    }

    @Test
    @DisplayName("Должен записать аудит обновления черновика")
    void shouldAuditDraftUpdate() {
        // Given
        Long draftId = 3L;
        Long userId = 100L;
        Long newRuleId = 5L;

        var existingDraft = Draft.builder()
                .id(draftId)
                .barcode("DRF-260209-333333")
                .draftCreatorId(userId)
                .senderUserId(userId)
                .recipientUserId(200L)
                .recipientAddress("Старый адрес")
                .pricingRuleId(1L)
                .estimatedPrice(new BigDecimal("350.00"))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findById(draftId)).thenReturn(Optional.of(existingDraft));
        when(repository.save(any(Draft.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pricingRuleService.calculatePrice(newRuleId)).thenReturn(new BigDecimal("650.00"));

        var command = UpdateDraftCommand.builder()
                .draftId(draftId)
                .userId(userId)
                .recipientUserId(300L)
                .recipientAddress("Новый адрес")
                .pricingRuleId(newRuleId)
                .build();

        // When
        updateDraftService.update(command);

        // Then
        verify(auditLogUseCase).create(auditCaptor.capture());

        var auditCommand = auditCaptor.getValue();
        assertThat(auditCommand.actionTypeName()).isEqualTo("DRAFT_UPDATE");
        assertThat(auditCommand.userId()).isEqualTo(userId);
        assertThat(auditCommand.tableName()).isEqualTo("waybill_drafts");
        assertThat(auditCommand.recordId()).isEqualTo(draftId);
        assertThat(auditCommand.newValues())
                .containsEntry("draftId", draftId)
                .containsEntry("recipientUserId", 300L)
                .containsEntry("recipientAddress", "Новый адрес")
                .containsEntry("pricingRuleId", newRuleId);
    }

    @Test
    @DisplayName("Должен записать в аудит только измененные поля")
    void shouldAuditWithOnlyChangedFields() {
        // Given
        Long draftId = 4L;
        Long userId = 100L;

        var existingDraft = Draft.builder()
                .id(draftId)
                .barcode("DRF-260209-444444")
                .draftCreatorId(userId)
                .senderUserId(userId)
                .recipientUserId(200L)
                .recipientAddress("Старый адрес")
                .pricingRuleId(1L)
                .estimatedPrice(new BigDecimal("350.00"))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findById(draftId)).thenReturn(Optional.of(existingDraft));
        when(repository.save(any(Draft.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var command = UpdateDraftCommand.builder()
                .draftId(draftId)
                .userId(userId)
                .recipientAddress("Только новый адрес")
                .build();

        // When
        updateDraftService.update(command);

        // Then
        verify(auditLogUseCase).create(auditCaptor.capture());

        var newValues = auditCaptor.getValue().newValues();
        assertThat(newValues)
                .containsEntry("draftId", draftId)
                .containsEntry("recipientAddress", "Только новый адрес");
        assertThat(newValues).doesNotContainKeys("recipientUserId", "pricingRuleId");
    }

}
