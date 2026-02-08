package com.logistics.corebusiness.waybill.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Доменная сущность подтвержденной накладной.
 * Создается из WaybillDraft после приемки посылки на ПВЗ.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Waybill {

    private Long id;
    private String waybillNumber;
    private Long waybillCreatorId;
    private Long senderUserId;
    private Long recipientUserId;
    private String recipientAddress;
    private BigDecimal weightActual;
    private Dimensions dimensions;
    private Long pricingRuleId;
    private BigDecimal finalPrice;
    private WaybillStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
}
