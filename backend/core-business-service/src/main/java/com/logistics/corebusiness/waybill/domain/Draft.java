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
 * 1. Клиент выбирает тарифный план (pricingRuleId) и указывает адрес
 * 2. Система генерирует barcode и рассчитывает estimatedPrice = basePrice тарифа
 * 3. Статус: PENDING (ожидает приемки)
 * 4. При приемке: создается Waybill, черновик переходит в CONFIRMED
 * 5. Может быть отменен (CANCELLED) до приемки
 *
 * <h2>Ключевые поля</h2>
 * - barcode: Уникальный штрих-код для идентификации черновика
 * - pricingRuleId: Ссылка на тарифный план (несёт в себе категорию веса и габаритов)
 * - estimatedPrice: Предварительная цена = basePrice выбранного тарифа
 *
 * @see Waybill для подтвержденной накладной
 * @see DraftStatus для возможных статусов
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Draft {

    private Long id;
    private String barcode;
    private Long draftCreatorId;
    private Long senderUserId;
    private Long recipientUserId;
    private String recipientAddress;
    private Long pricingRuleId;
    private BigDecimal estimatedPrice;
    private DraftStatus draftStatus;
    private LocalDateTime createdAt;
}