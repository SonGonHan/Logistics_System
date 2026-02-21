package com.logistics.corebusiness.waybill.adapter.in;

import com.logistics.corebusiness.waybill.adapter.in.web.dto.DetailedDraftResponse;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.DimensionsDto;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.DraftResponse;
import com.logistics.corebusiness.waybill.domain.Dimensions;
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
        var dimensions = new Dimensions(
                new BigDecimal("30"),
                new BigDecimal("40"),
                new BigDecimal("50")
        );

        var createdAt = LocalDateTime.of(2026, 2, 9, 12, 30);

        var draft = Draft.builder()
                .id(1L)
                .barcode("DRF-260209-123456")
                .draftCreatorId(100L)
                .senderUserId(100L)
                .recipientUserId(200L)
                .recipientAddress("г. Москва, ул. Тестовая, д. 1")
                .weightDeclared(new BigDecimal("2.5"))
                .dimensions(dimensions)
                .pricingRuleId(10L)
                .estimatedPrice(new BigDecimal("1250.00"))
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
        assertThat(response.weightDeclared()).isEqualByComparingTo("2.5");
        assertThat(response.estimatedPrice()).isEqualByComparingTo("1250.00");
        assertThat(response.draftStatus()).isEqualTo(DraftStatus.PENDING);
        assertThat(response.createdAt()).isEqualTo(createdAt);

        // Проверяем dimensions
        assertThat(response.dimensions()).isNotNull();
        assertThat(response.dimensions().length()).isEqualByComparingTo("30");
        assertThat(response.dimensions().width()).isEqualByComparingTo("40");
        assertThat(response.dimensions().height()).isEqualByComparingTo("50");
    }

    @Test
    @DisplayName("Должен корректно преобразовать Draft в DetailedDraftResponse")
    void shouldMapDraftToDetailedDraftResponse() {
        // Given
        var dimensions = new Dimensions(
                new BigDecimal("30"),
                new BigDecimal("40"),
                new BigDecimal("50")
        );

        var createdAt = LocalDateTime.of(2026, 2, 9, 12, 30);

        var draft = Draft.builder()
                .id(2L)
                .barcode("DRF-260209-987654")
                .draftCreatorId(100L)
                .senderUserId(150L)
                .recipientUserId(200L)
                .recipientAddress("г. Санкт-Петербург, ул. Невская, д. 5")
                .weightDeclared(new BigDecimal("3.0"))
                .dimensions(dimensions)
                .pricingRuleId(20L)
                .estimatedPrice(new BigDecimal("1500.00"))
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
        assertThat(response.weightDeclared()).isEqualByComparingTo("3.0");
        assertThat(response.pricingRuleId()).isEqualTo(20L);
        assertThat(response.estimatedPrice()).isEqualByComparingTo("1500.00");
        assertThat(response.draftStatus()).isEqualTo(DraftStatus.CONFIRMED);
        assertThat(response.createdAt()).isEqualTo(createdAt);

        // Проверяем dimensions
        assertThat(response.dimensions()).isNotNull();
        assertThat(response.dimensions().length()).isEqualByComparingTo("30");
        assertThat(response.dimensions().width()).isEqualByComparingTo("40");
        assertThat(response.dimensions().height()).isEqualByComparingTo("50");
    }

    @Test
    @DisplayName("Должен обработать null dimensions при маппинге в DraftResponse")
    void shouldHandleNullDimensionsInDraftResponse() {
        // Given
        var draft = Draft.builder()
                .id(3L)
                .barcode("DRF-260209-111111")
                .draftCreatorId(100L)
                .senderUserId(100L)
                .recipientUserId(200L)
                .recipientAddress("Адрес")
                .weightDeclared(new BigDecimal("1.0"))
                .dimensions(null)
                .estimatedPrice(new BigDecimal("500.00"))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        DraftResponse response = DraftControllerMapper.toResponse(draft);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.dimensions()).isNull();
    }

    @Test
    @DisplayName("Должен обработать null dimensions при маппинге в DetailedDraftResponse")
    void shouldHandleNullDimensionsInDetailedDraftResponse() {
        // Given
        var draft = Draft.builder()
                .id(4L)
                .barcode("DRF-260209-222222")
                .draftCreatorId(100L)
                .senderUserId(100L)
                .recipientUserId(200L)
                .recipientAddress("Адрес")
                .weightDeclared(new BigDecimal("1.0"))
                .dimensions(null)
                .pricingRuleId(null)
                .estimatedPrice(new BigDecimal("500.00"))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        DetailedDraftResponse response = DraftControllerMapper.toDetailedResponse(draft);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.dimensions()).isNull();
        assertThat(response.pricingRuleId()).isNull();
    }

    @Test
    @DisplayName("Должен корректно преобразовать Dimensions в DimensionsDto")
    void shouldMapDimensionsToDimensionsDto() {
        // Given
        var dimensions = new Dimensions(
                new BigDecimal("10.5"),
                new BigDecimal("20.3"),
                new BigDecimal("30.7")
        );

        // When
        DimensionsDto dto = DraftControllerMapper.toDimensionsDto(dimensions);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.length()).isEqualByComparingTo("10.5");
        assertThat(dto.width()).isEqualByComparingTo("20.3");
        assertThat(dto.height()).isEqualByComparingTo("30.7");
    }

    @Test
    @DisplayName("Должен вернуть null при преобразовании null Dimensions в DimensionsDto")
    void shouldReturnNullWhenDimensionsIsNull() {
        // When
        DimensionsDto dto = DraftControllerMapper.toDimensionsDto(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("Должен корректно преобразовать DimensionsDto в Dimensions")
    void shouldMapDimensionsDtoToDimensions() {
        // Given
        var dto = DimensionsDto.builder()
                .length(new BigDecimal("15.0"))
                .width(new BigDecimal("25.0"))
                .height(new BigDecimal("35.0"))
                .build();

        // When
        Dimensions dimensions = DraftControllerMapper.toDimensions(dto);

        // Then
        assertThat(dimensions).isNotNull();
        assertThat(dimensions.length()).isEqualByComparingTo("15.0");
        assertThat(dimensions.width()).isEqualByComparingTo("25.0");
        assertThat(dimensions.height()).isEqualByComparingTo("35.0");
    }

    @Test
    @DisplayName("Должен вернуть null при преобразовании null DimensionsDto в Dimensions")
    void shouldReturnNullWhenDimensionsDtoIsNull() {
        // When
        Dimensions dimensions = DraftControllerMapper.toDimensions(null);

        // Then
        assertThat(dimensions).isNull();
    }
}
