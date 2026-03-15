package com.logistics.corebusiness.waybill.application.port.in;

import com.logistics.corebusiness.waybill.adapter.in.web.dto.WaybillResponse;
import com.logistics.corebusiness.waybill.application.port.in.command.UpdateWaybillStatusCommand;
import com.logistics.corebusiness.waybill.application.usecase.UpdateWaybillStatusService;
import com.logistics.corebusiness.waybill.domain.Waybill;

/**
 * Порт (интерфейс) для изменения статуса подтвержденной накладной.
 *
 * <h2>Назначение</h2>
 * Определяет контракт допустимого перевода накладной между статусами.
 *
 * <h2>Реализации</h2>
 * - {@link UpdateWaybillStatusService}
 *
 * @see Waybill для доменной модели
 * @see UpdateWaybillStatusCommand для параметров изменения
 */
public interface UpdateWaybillStatusUseCase {

    /**
     * Обновить статус накладной.
     *
     * @param command параметры изменения
     * @return обновленная накладная
     */
    WaybillResponse updateStatus(UpdateWaybillStatusCommand command);
}
