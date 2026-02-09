package com.logistics.corebusiness.waybill.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Доменная сущность черновика накладной (draft).
 *
 * <h2>Назначение</h2>
 * Представляет предварительную заявку на отправку посылки.
 * Создается клиентом до физической приемки посылки на ПВЗ.
 *
 * <h2>Жизненный цикл</h2>
 * 1. Клиент заполняет данные о посылке (получатель, адрес, заявленный вес)
 * 2. Система генерирует barcode для идентификации
 * 3. Рассчитывается предварительная цена (estimatedPrice)
 * 4. Статус: PENDING (ожидает приемки)
 * 5. При приемке: создается Waybill, черновик переходит в CONFIRMED
 * 6. Может быть отменен (CANCELLED) до приемки
 *
 * <h2>Ключевые поля</h2>
 * - barcode: Уникальный штрих-код для идентификации черновика
 * - weightDeclared: Вес, заявленный клиентом (может отличаться от фактического)
 * - estimatedPrice: Предварительная цена (будет пересчитана при приемке)
 * - draftStatus: Статус черновика (PENDING, CONFIRMED, CANCELLED)
 *
 * @see Waybill для подтвержденной накладной
 * @see DraftStatus для возможных статусов
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
