package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.waybill.adapter.in.web.dto.DraftResponse;
import com.logistics.corebusiness.waybill.application.exception.DraftNotFoundException;
import com.logistics.corebusiness.waybill.application.port.in.command.UpdateDraftCommand;
import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.domain.Dimensions;
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

    @Captor
    private ArgumentCaptor<Draft> draftCaptor;

    private UpdateDraftService updateDraftService;

    @BeforeEach
    void setUp() {
        updateDraftService = new UpdateDraftService(repository, pricingRuleService);
    }

    @Test
    @DisplayName("Должен успешно обновить черновик")
    void shouldUpdateDraftSuccessfully() {
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
                .weightDeclared(new BigDecimal("2.0"))
                .dimensions(null)
                .estimatedPrice(new BigDecimal("1000.00"))
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
                .weightDeclared(null)
                .dimensions(null)
                .build();

        // When
        DraftResponse result = updateDraftService.update(command);

        // Then
        verify(repository).findById(draftId);
        verify(repository).save(draftCaptor.capture());

        Draft updatedDraft = draftCaptor.getValue();
        assertThat(updatedDraft.getRecipientUserId()).isEqualTo(300L);
        assertThat(updatedDraft.getRecipientAddress()).isEqualTo("Новый адрес");
        assertThat(updatedDraft.getWeightDeclared()).isEqualByComparingTo("2.0"); // Не изменился
        assertThat(updatedDraft.getEstimatedPrice()).isEqualByComparingTo("1000.00"); // Не пересчиталась

        assertThat(result).isNotNull();
        assertThat(result.recipientAddress()).isEqualTo("Новый адрес");

        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Должен пересчитать цену при изменении веса")
    void shouldRecalculatePriceWhenWeightChanges() {
        // Given
        Long draftId = 2L;
        Long userId = 100L;
        Long pricingRuleId = 1L;

        var existingDraft = Draft.builder()
                .id(draftId)
                .barcode("DRF-260209-222222")
                .draftCreatorId(userId)
                .senderUserId(userId)
                .recipientUserId(200L)
                .recipientAddress("Адрес")
                .weightDeclared(new BigDecimal("2.0"))
                .pricingRuleId(pricingRuleId)
                .dimensions(null)
                .estimatedPrice(new BigDecimal("1000.00"))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findById(draftId)).thenReturn(Optional.of(existingDraft));
        when(repository.save(any(Draft.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pricingRuleService.calculatePrice(pricingRuleId, new BigDecimal("5.0")))
                .thenReturn(new BigDecimal("2500.00"));

        var command = UpdateDraftCommand.builder()
                .draftId(draftId)
                .userId(userId)
                .recipientUserId(null)
                .recipientAddress(null)
                .weightDeclared(new BigDecimal("5.0"))
                .dimensions(null)
                .pricingRuleId(null)
                .build();

        // When
        DraftResponse result = updateDraftService.update(command);

        // Then
        verify(pricingRuleService).calculatePrice(pricingRuleId, new BigDecimal("5.0"));
        verify(repository).save(draftCaptor.capture());

        Draft updatedDraft = draftCaptor.getValue();
        assertThat(updatedDraft.getWeightDeclared()).isEqualByComparingTo("5.0");
        // Новая цена: 5.0 * 500.00 = 2500.00
        assertThat(updatedDraft.getEstimatedPrice()).isEqualByComparingTo("2500.00");
    }

    @Test
    @DisplayName("Должен пересчитать цену при изменении габаритов")
    void shouldRecalculatePriceWhenDimensionsChange() {
        // Given
        Long draftId = 3L;
        Long userId = 100L;
        Long pricingRuleId = 1L;

        var existingDraft = Draft.builder()
                .id(draftId)
                .barcode("DRF-260209-333333")
                .draftCreatorId(userId)
                .senderUserId(userId)
                .recipientUserId(200L)
                .recipientAddress("Адрес")
                .weightDeclared(new BigDecimal("3.0"))
                .pricingRuleId(pricingRuleId)
                .dimensions(null)
                .estimatedPrice(new BigDecimal("1500.00"))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findById(draftId)).thenReturn(Optional.of(existingDraft));
        when(repository.save(any(Draft.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pricingRuleService.calculatePrice(pricingRuleId, new BigDecimal("3.0")))
                .thenReturn(new BigDecimal("1500.00"));

        var newDimensions = new Dimensions(
                new BigDecimal("30"),
                new BigDecimal("40"),
                new BigDecimal("50")
        );

        var command = UpdateDraftCommand.builder()
                .draftId(draftId)
                .userId(userId)
                .recipientUserId(null)
                .recipientAddress(null)
                .weightDeclared(null)
                .dimensions(newDimensions)
                .pricingRuleId(null)
                .build();

        // When
        updateDraftService.update(command);

        // Then
        verify(pricingRuleService).calculatePrice(pricingRuleId, new BigDecimal("3.0"));
        verify(repository).save(draftCaptor.capture());

        Draft updatedDraft = draftCaptor.getValue();
        assertThat(updatedDraft.getDimensions()).isEqualTo(newDimensions);
        // Цена пересчиталась: 3.0 * 500.00 = 1500.00
        assertThat(updatedDraft.getEstimatedPrice()).isEqualByComparingTo("1500.00");
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
    }

}
