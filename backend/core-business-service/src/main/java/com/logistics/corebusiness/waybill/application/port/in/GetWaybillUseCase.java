package com.logistics.corebusiness.waybill.application.port.in;

import com.logistics.corebusiness.waybill.adapter.in.web.dto.WaybillResponse;
import com.logistics.corebusiness.waybill.application.port.in.command.GetWaybillCommand;
import com.logistics.corebusiness.waybill.application.usecase.GetWaybillService;
import com.logistics.corebusiness.waybill.domain.Waybill;

/**
 * Порт (интерфейс) для получения подтвержденной накладной.
 *
 * <h2>Назначение</h2>
 * Определяет контракт поиска накладной по ID или номеру.
 *
 * <h2>Реализации</h2>
 * - {@link GetWaybillService}
 *
 * @see Waybill для доменной модели
 * @see GetWaybillCommand для параметров поиска
 */
public interface GetWaybillUseCase {

    /**
     * Получить накладную.
     *
     * @param command параметры поиска
     * @return найденная накладная
     */
    WaybillResponse get(GetWaybillCommand command);
}
