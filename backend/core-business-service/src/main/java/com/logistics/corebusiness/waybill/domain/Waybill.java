package com.logistics.corebusiness.waybill.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Доменная сущность подтвержденной накладной (waybill).
 *
 * <h2>Назначение</h2>
 * Представляет подтвержденную накладную после приемки посылки на ПВЗ.
 * Создается из Draft после того, как посылка физически принята.
 *
 * <h2>Жизненный цикл</h2>
 * 1. Клиент создает черновик (Draft) с выбранным тарифом
 * 2. Оператор ПВЗ принимает посылку и подтверждает тариф
 * 3. Система создает Waybill — finalPrice = basePrice тарифного плана
 * 4. Накладная получает уникальный номер (waybillNumber) для отслеживания
 * 5. История изменений статуса записывается в WaybillStatusHistory
 *
 * <h2>Ключевые поля</h2>
 * - waybillNumber: Уникальный номер накладной для отслеживания
 * - pricingRuleId: Тарифный план (включает категорию веса и размеров)
 * - finalPrice: Итоговая цена = basePrice тарифа
 * - status: Текущий статус (ACCEPTED_AT_PVZ, IN_TRANSIT, DELIVERED и т.д.)
 *
 * @see Draft для черновика до приемки
 * @see WaybillStatus для возможных статусов
 * @see WaybillStatusHistory для истории изменений
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
    private Long pricingRuleId;
    private BigDecimal finalPrice;
    private WaybillStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
}