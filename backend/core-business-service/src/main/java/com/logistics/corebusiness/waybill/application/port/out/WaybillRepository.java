package com.logistics.corebusiness.waybill.application.port.out;

import com.logistics.corebusiness.waybill.domain.Waybill;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;

import java.util.List;
import java.util.Optional;

/**
 * Порт для работы с репозиторием накладных.
 */
public interface WaybillRepository {

    Waybill save(Waybill waybill);

    void delete(Waybill waybill);

    Optional<Waybill> findById(Long id);

    Optional<Waybill> findByWaybillNumber(String waybillNumber);

    List<Waybill> findBySenderUserId(Long senderUserId);

    List<Waybill> findByRecipientUserId(Long recipientUserId);

    List<Waybill> findByStatus(WaybillStatus status);

    List<Waybill> findByWaybillCreatorId(Long creatorId);
}
