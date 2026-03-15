package com.logistics.corebusiness.waybill.application.port.in;

import com.logistics.corebusiness.waybill.adapter.in.web.dto.WaybillResponse;
import com.logistics.corebusiness.waybill.application.port.in.command.CreateWaybillCommand;
import com.logistics.corebusiness.waybill.application.usecase.CreateWaybillService;
import com.logistics.corebusiness.waybill.domain.Waybill;

/**
 * Порт (интерфейс) для создания подтвержденной накладной.
 *
 * <h2>Назначение</h2>
 * Определяет контракт создания накладной из черновика или напрямую.
 *
 * <h2>Реализации</h2>
 * - {@link CreateWaybillService}
 *
 * @see Waybill для доменной модели
 * @see CreateWaybillCommand для параметров
 */
public interface CreateWaybillUseCase {

    /**
     * Создать накладную.
     *
     * @param command параметры создания
     * @return созданная накладная в виде WaybillResponse
     */
    WaybillResponse create(CreateWaybillCommand command);
}
