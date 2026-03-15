package com.logistics.corebusiness.waybill.application.port.in;

import com.logistics.corebusiness.waybill.adapter.in.web.dto.WaybillResponse;
import com.logistics.corebusiness.waybill.application.port.in.command.ListWaybillsCommand;
import com.logistics.corebusiness.waybill.application.usecase.ListWaybillsService;
import com.logistics.corebusiness.waybill.domain.Waybill;

import java.util.List;

/**
 * Порт (интерфейс) для получения списка подтвержденных накладных.
 *
 * <h2>Назначение</h2>
 * Определяет контракт выборки накладных отправителя.
 *
 * <h2>Реализации</h2>
 * - {@link ListWaybillsService}
 *
 * @see Waybill для доменной модели
 * @see ListWaybillsCommand для параметров выборки
 */
public interface ListWaybillsUseCase {

    /**
     * Получить список накладных.
     *
     * @param command параметры выборки
     * @return список накладных
     */
    List<WaybillResponse> list(ListWaybillsCommand command);
}
