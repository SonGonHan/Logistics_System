package com.logistics.corebusiness.waybill.application.port.out;

import com.logistics.corebusiness.waybill.domain.DraftStatus;
import com.logistics.corebusiness.waybill.domain.WaybillDraft;

import java.util.List;
import java.util.Optional;

/**
 * Порт для работы с репозиторием черновиков накладных.
 */
public interface WaybillDraftRepository {

    WaybillDraft save(WaybillDraft draft);

    void delete(WaybillDraft draft);

    Optional<WaybillDraft> findById(Long id);

    Optional<WaybillDraft> findByBarcode(String barcode);

    List<WaybillDraft> findBySenderUserId(Long senderUserId);

    List<WaybillDraft> findByRecipientUserId(Long recipientUserId);

    List<WaybillDraft> findByDraftStatus(DraftStatus status);

    List<WaybillDraft> findByDraftCreatorId(Long creatorId);
}
