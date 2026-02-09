package com.logistics.corebusiness.waybill.domain;

/**
 * Перечисление статусов накладной на всех этапах доставки.
 *
 * <h2>Жизненный цикл нормальной доставки</h2>
 * ACCEPTED_AT_PVZ → IN_TRANSIT → AT_SORTING_CENTER → OUT_FOR_DELIVERY → DELIVERED
 *
 * <h2>Альтернативные сценарии</h2>
 * - Самовывоз: → READY_FOR_PICKUP → DELIVERED
 * - Возврат: → RETURNING → RETURNED
 * - Проблемы: → CANCELLED или LOST
 *
 * <h2>Описание статусов</h2>
 * - ACCEPTED_AT_PVZ: Посылка принята на пункте выдачи заказов
 * - IN_TRANSIT: В пути между объектами (склады, ПВЗ)
 * - AT_SORTING_CENTER: На сортировочном центре
 * - OUT_FOR_DELIVERY: У курьера на доставке
 * - READY_FOR_PICKUP: Готова к самовывозу на ПВЗ
 * - DELIVERED: Доставлена получателю
 * - RETURNING: Возвращается отправителю
 * - RETURNED: Возвращена отправителю
 * - CANCELLED: Отменена
 * - LOST: Потеряна в процессе доставки
 *
 * @see Waybill для накладной
 * @see WaybillStatusHistory для истории изменений
 */
public enum WaybillStatus {
    ACCEPTED_AT_PVZ,

    IN_TRANSIT,

    AT_SORTING_CENTER,

    OUT_FOR_DELIVERY,

    READY_FOR_PICKUP,

    DELIVERED,

    RETURNING,

    RETURNED,

    CANCELLED,

    LOST
}
