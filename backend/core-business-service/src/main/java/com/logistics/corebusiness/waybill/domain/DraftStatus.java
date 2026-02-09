package com.logistics.corebusiness.waybill.domain;

/**
 * Перечисление статусов черновика накладной.
 *
 * <h2>Жизненный цикл черновика</h2>
 * PENDING → CONFIRMED (при успешной приемке)
 * PENDING → CANCELLED (при отмене клиентом или системой)
 *
 * <h2>Описание статусов</h2>
 * - PENDING: Ожидает приемки на ПВЗ (начальный статус)
 * - CONFIRMED: Подтвержден (создана накладная Waybill)
 * - CANCELLED: Отменен до приемки
 *
 * @see WaybillDraft для черновика
 * @see Waybill для подтвержденной накладной
 */
public enum DraftStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}
