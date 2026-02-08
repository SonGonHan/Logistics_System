package com.logistics.corebusiness.waybill.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Доменная сущность черновика накладной.
 * Создается до приемки посылки на ПВЗ.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WaybillDraft {

    private Long id;
    private String barcode;
    private Long draftCreatorId;
    private Long senderUserId;
    private Long recipientUserId;
    private String recipientAddress;
    private BigDecimal weightDeclared;
    private Dimensions dimensions;
    private Long pricingRuleId;
    private BigDecimal estimatedPrice;
    private DraftStatus draftStatus;
    private LocalDateTime createdAt;
}
