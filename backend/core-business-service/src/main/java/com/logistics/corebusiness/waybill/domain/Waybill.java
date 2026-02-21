package com.logistics.corebusiness.waybill.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Доменная сущность подтвержденной накладной (waybill).
 *
 * <h2>Назначение</h2>
 * Представляет подтвержденную накладную после приемки посылки на ПВЗ.
 * Создается из Draft после того, как посылка физически принята и взвешена.
 *
 * <h2>Жизненный цикл</h2>
 * 1. Клиент создает черновик (Draft) через веб/мобильное приложение
 * 2. Оператор ПВЗ принимает посылку, взвешивает, проверяет габариты
 * 3. Система создает Waybill с актуальными данными (вес, цена)
 * 4. Накладная получает уникальный номер (waybillNumber) для отслеживания
 * 5. История изменений статуса записывается в WaybillStatusHistory
 *
 * <h2>Ключевые поля</h2>
 * - waybillNumber: Уникальный номер накладной для отслеживания
 * - weightActual: Реальный вес после взвешивания (может отличаться от заявленного)
 * - finalPrice: Итоговая цена доставки (рассчитана по pricing rule)
 * - status: Текущий статус (ACCEPTED_AT_PVZ, IN_TRANSIT, DELIVERED и т.д.)
 * - acceptedAt: Дата/время приемки на ПВЗ
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
    private BigDecimal weightActual;
    private Dimensions dimensions;
    private Long pricingRuleId;
    private BigDecimal finalPrice;
    private WaybillStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
}
