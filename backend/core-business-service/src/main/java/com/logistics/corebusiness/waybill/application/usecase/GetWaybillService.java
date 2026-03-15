package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.waybill.adapter.in.WaybillControllerMapper;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.WaybillResponse;
import com.logistics.corebusiness.waybill.application.exception.WaybillNotFoundException;
import com.logistics.corebusiness.waybill.application.port.in.GetWaybillUseCase;
import com.logistics.corebusiness.waybill.application.port.in.command.GetWaybillCommand;
import com.logistics.corebusiness.waybill.application.port.out.WaybillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис получения подтвержденной накладной.
 *
 * <h2>Назначение</h2>
 * Возвращает накладную по ID или номеру.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class GetWaybillService implements GetWaybillUseCase {

    private final WaybillRepository waybillRepository;

    @Override
    public WaybillResponse get(GetWaybillCommand command) {
        if (command.id() != null) {
            var waybill = waybillRepository.findById(command.id())
                    .orElseThrow(() -> WaybillNotFoundException.byId(command.id()));
            return WaybillControllerMapper.toResponse(waybill);
        }

        var waybill = waybillRepository.findByWaybillNumber(command.waybillNumber())
                .orElseThrow(() -> WaybillNotFoundException.byNumber(command.waybillNumber()));
        return WaybillControllerMapper.toResponse(waybill);
    }
}
