package com.logistics.corebusiness.waybill.adapter.in;

import com.logistics.corebusiness.waybill.domain.Waybill;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WaybillControllerMapper - тестирование маппера Waybill -> DTO")
class WaybillControllerMapperTest {

    @Test
    @DisplayName("Должен корректно маппить все поля накладной")
    void shouldMapAllFieldsCorrectly() {
        var createdAt = LocalDateTime.of(2026, 3, 15, 12, 0);
        var acceptedAt = LocalDateTime.of(2026, 3, 15, 12, 5);

        var waybill = Waybill.builder()
                .id(1L)
                .waybillNumber("WB-260315-123456")
                .waybillCreatorId(10L)
                .senderUserId(20L)
                .recipientUserId(30L)
                .recipientAddress("Москва, ул. Ленина, д. 10")
                .pricingRuleId(40L)
                .finalPrice(new BigDecimal("750.00"))
                .status(WaybillStatus.IN_TRANSIT)
                .createdAt(createdAt)
                .acceptedAt(acceptedAt)
                .build();

        var response = WaybillControllerMapper.toResponse(waybill);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.waybillNumber()).isEqualTo("WB-260315-123456");
        assertThat(response.waybillCreatorId()).isEqualTo(10L);
        assertThat(response.senderUserId()).isEqualTo(20L);
        assertThat(response.recipientUserId()).isEqualTo(30L);
        assertThat(response.recipientAddress()).isEqualTo("Москва, ул. Ленина, д. 10");
        assertThat(response.pricingRuleId()).isEqualTo(40L);
        assertThat(response.finalPrice()).isEqualByComparingTo("750.00");
        assertThat(response.status()).isEqualTo(WaybillStatus.IN_TRANSIT);
        assertThat(response.createdAt()).isEqualTo(createdAt);
        assertThat(response.acceptedAt()).isEqualTo(acceptedAt);
    }
}
