package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.waybill.adapter.in.WaybillControllerMapper;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.WaybillResponse;
import com.logistics.corebusiness.waybill.application.port.in.ListWaybillsUseCase;
import com.logistics.corebusiness.waybill.application.port.in.command.ListWaybillsCommand;
import com.logistics.corebusiness.waybill.application.port.out.WaybillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис получения списка подтвержденных накладных.
 *
 * <h2>Назначение</h2>
 * Возвращает список накладных отправителя.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ListWaybillsService implements ListWaybillsUseCase {

    private final WaybillRepository waybillRepository;

    @Override
    public List<WaybillResponse> list(ListWaybillsCommand command) {
        return waybillRepository.findBySenderUserId(command.senderUserId()).stream()
                .map(WaybillControllerMapper::toResponse)
                .toList();
    }
}
