package com.logistics.corebusiness.waybill.adapter.in;

import com.logistics.corebusiness.waybill.adapter.in.web.dto.DetailedDraftResponse;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.DraftResponse;
import com.logistics.corebusiness.waybill.domain.Draft;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DraftControllerMapper - тестирование маппера Draft ↔ DTO")
class DraftControllerMapperTest {

    @Test
    @DisplayName("Должен корректно преобразовать Draft в DraftResponse")
    void shouldMapDraftToDraftResponse() {
        // Given
        var createdAt = LocalDateTime.of(2026, 2, 9, 12, 30);

        var draft = Draft.builder()
                .id(1L)
                .barcode("DRF-260209-123456")
                .draftCreatorId(100L)
                .senderUserId(100L)
                .recipientUserId(200L)
                .recipientAddress("г. Москва, ул. Тестовая, д. 1")
                .pricingRuleId(10L)
                .estimatedPrice(new BigDecimal("350.00"))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(createdAt)
                .build();

        // When
        DraftResponse response = DraftControllerMapper.toResponse(draft);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.barcode()).isEqualTo("DRF-260209-123456");
        assertThat(response.recipientUserId()).isEqualTo(200L);
        assertThat(response.recipientAddress()).isEqualTo("г. Москва, ул. Тестовая, д. 1");
        assertThat(response.estimatedPrice()).isEqualByComparingTo("350.00");
        assertThat(response.draftStatus()).isEqualTo(DraftStatus.PENDING);
        assertThat(response.createdAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("Должен корректно преобразовать Draft в DetailedDraftResponse")
    void shouldMapDraftToDetailedDraftResponse() {
        // Given
        var createdAt = LocalDateTime.of(2026, 2, 9, 12, 30);

        var draft = Draft.builder()
                .id(2L)
                .barcode("DRF-260209-987654")
                .draftCreatorId(100L)
                .senderUserId(150L)
                .recipientUserId(200L)
                .recipientAddress("г. Санкт-Петербург, ул. Невская, д. 5")
                .pricingRuleId(20L)
                .estimatedPrice(new BigDecimal("650.00"))
                .draftStatus(DraftStatus.CONFIRMED)
                .createdAt(createdAt)
                .build();

        // When
        DetailedDraftResponse response = DraftControllerMapper.toDetailedResponse(draft);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.barcode()).isEqualTo("DRF-260209-987654");
        assertThat(response.draftCreatorId()).isEqualTo(100L);
        assertThat(response.senderUserId()).isEqualTo(150L);
        assertThat(response.recipientUserId()).isEqualTo(200L);
        assertThat(response.recipientAddress()).isEqualTo("г. Санкт-Петербург, ул. Невская, д. 5");
        assertThat(response.pricingRuleId()).isEqualTo(20L);
        assertThat(response.estimatedPrice()).isEqualByComparingTo("650.00");
        assertThat(response.draftStatus()).isEqualTo(DraftStatus.CONFIRMED);
        assertThat(response.createdAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("Должен корректно маппить Draft с null pricingRuleId в DetailedDraftResponse")
    void shouldHandleNullPricingRuleIdInDetailedResponse() {
        // Given
        var draft = Draft.builder()
                .id(4L)
                .barcode("DRF-260209-222222")
                .draftCreatorId(100L)
                .senderUserId(100L)
                .recipientUserId(200L)
                .recipientAddress("Адрес")
                .pricingRuleId(null)
                .estimatedPrice(new BigDecimal("150.00"))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        DetailedDraftResponse response = DraftControllerMapper.toDetailedResponse(draft);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.pricingRuleId()).isNull();
    }
}