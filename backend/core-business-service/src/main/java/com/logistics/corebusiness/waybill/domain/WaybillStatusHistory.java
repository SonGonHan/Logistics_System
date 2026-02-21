package com.logistics.corebusiness.waybill.domain;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Доменная сущность записи в истории изменений статуса накладной.
 *
 * <h2>Назначение</h2>
 * Обеспечивает полный аудит движения посылки по логистической цепочке.
 * Каждое изменение статуса накладной создает новую запись в истории.
 *
 * <h2>Примеры использования</h2>
 * - Отслеживание: Клиент видит где находится его посылка
 * - Аналитика: Время на каждом этапе доставки
 * - Расследование: Кто, когда и где изменил статус посылки
 * - Регуляторика: Доказательства выполнения обязательств
 *
 * <h2>Ключевые поля</h2>
 * - waybillId: Ссылка на накладную
 * - status: Новый статус (IN_TRANSIT, DELIVERED и т.д.)
 * - facilityId: На каком объекте произошло изменение (склад, ПВЗ)
 * - changedBy: Кто изменил (userId оператора/курьера)
 * - changedAt: Точное время изменения
 * - notes: Дополнительная информация (например, "Получатель не найден")
 *
 * @see Waybill для накладной
 * @see WaybillStatus для возможных статусов
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WaybillStatusHistory {

    private Long id;
    private Long waybillId;
    private WaybillStatus status;
    private Long facilityId;
    private String notes;
    private Long changedBy;
    private LocalDateTime changedAt;
}
