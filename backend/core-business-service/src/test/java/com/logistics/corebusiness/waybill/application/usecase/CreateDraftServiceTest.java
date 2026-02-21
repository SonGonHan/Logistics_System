package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.waybill.application.port.in.command.CreateDraftCommand;
import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.application.port.out.RecipientUserPort;
import com.logistics.corebusiness.waybill.application.util.BarcodeGenerator;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateDraftService - тестирование создания черновика накладной")
class CreateDraftServiceTest {

    @Mock
    private DraftRepository repository;

    @Mock
    private BarcodeGenerator barcodeGenerator;

    @Mock
    private PricingRuleService pricingRuleService;

    @Mock
    private RecipientUserPort recipientUserPort;

    @Captor
    private ArgumentCaptor<Draft> draftCaptor;

    private CreateDraftService createDraftService;

    @BeforeEach
    void setUp() {
        createDraftService = new CreateDraftService(
                repository,
                barcodeGenerator,
                pricingRuleService,
                recipientUserPort);
    }

    @Test
    @DisplayName("Должен успешно создать черновик с корректными полями")
    void shouldCreateDraftSuccessfully() {
        // Given
        String generatedBarcode = "DRF-260209-123456";
        Long pricingRuleId = 1L;
        when(barcodeGenerator.generate()).thenReturn(generatedBarcode);
        when(pricingRuleService.calculatePrice(pricingRuleId)).thenReturn(new BigDecimal("350.00"));
        when(recipientUserPort.findOrCreateByPhone(any())).thenReturn(3L);

        var command = CreateDraftCommand.builder()
                .draftCreatorId(1L)
                .senderUserId(2L)
                .recipientPhone("+79001234567")
                .recipientAddress("г. Москва, ул. Тестовая, д. 1")
                .pricingRuleId(pricingRuleId)
                .build();

        // When
        createDraftService.create(command);

        // Then
        verify(barcodeGenerator).generate();
        verify(pricingRuleService).calculatePrice(pricingRuleId);
        verify(repository).save(draftCaptor.capture());

        Draft savedDraft = draftCaptor.getValue();
        assertThat(savedDraft.getBarcode()).isEqualTo(generatedBarcode);
        assertThat(savedDraft.getDraftCreatorId()).isEqualTo(1L);
        assertThat(savedDraft.getSenderUserId()).isEqualTo(2L);
        assertThat(savedDraft.getRecipientUserId()).isEqualTo(3L);
        assertThat(savedDraft.getRecipientAddress()).isEqualTo("г. Москва, ул. Тестовая, д. 1");
        assertThat(savedDraft.getDraftStatus()).isEqualTo(DraftStatus.PENDING);
        assertThat(savedDraft.getCreatedAt()).isNotNull();
        assertThat(savedDraft.getPricingRuleId()).isEqualTo(pricingRuleId);
        assertThat(savedDraft.getEstimatedPrice()).isEqualByComparingTo("350.00");

        verifyNoMoreInteractions(repository, barcodeGenerator, pricingRuleService);
    }

    @Test
    @DisplayName("Должен рассчитать цену как basePrice тарифного плана")
    void shouldCalculateEstimatedPriceAsBasePrice() {
        // Given
        Long pricingRuleId = 2L;
        when(barcodeGenerator.generate()).thenReturn("DRF-260209-999999");
        when(pricingRuleService.calculatePrice(pricingRuleId)).thenReturn(new BigDecimal("650.00"));
        when(recipientUserPort.findOrCreateByPhone(any())).thenReturn(2L);

        var command = CreateDraftCommand.builder()
                .draftCreatorId(1L)
                .senderUserId(1L)
                .recipientPhone("+79001234568")
                .recipientAddress("г. Санкт-Петербург, ул. Невская, д. 5")
                .pricingRuleId(pricingRuleId)
                .build();

        // When
        createDraftService.create(command);

        // Then
        verify(pricingRuleService).calculatePrice(pricingRuleId);
        verify(repository).save(draftCaptor.capture());

        Draft savedDraft = draftCaptor.getValue();
        assertThat(savedDraft.getEstimatedPrice()).isEqualByComparingTo("650.00");
    }

    @Test
    @DisplayName("Должен установить начальный статус PENDING")
    void shouldSetInitialStatusToPending() {
        // Given
        Long pricingRuleId = 1L;
        when(barcodeGenerator.generate()).thenReturn("DRF-260209-222222");
        when(pricingRuleService.calculatePrice(pricingRuleId)).thenReturn(new BigDecimal("150.00"));
        when(recipientUserPort.findOrCreateByPhone(any())).thenReturn(11L);

        var command = CreateDraftCommand.builder()
                .draftCreatorId(10L)
                .senderUserId(10L)
                .recipientPhone("+79001234569")
                .recipientAddress("г. Екатеринбург, пр. Ленина, д. 50")
                .pricingRuleId(pricingRuleId)
                .build();

        // When
        createDraftService.create(command);

        // Then
        verify(repository).save(draftCaptor.capture());
        assertThat(draftCaptor.getValue().getDraftStatus()).isEqualTo(DraftStatus.PENDING);
    }
}