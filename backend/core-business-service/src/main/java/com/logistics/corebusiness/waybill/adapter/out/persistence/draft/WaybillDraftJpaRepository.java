package com.logistics.corebusiness.waybill.adapter.out.persistence.draft;

import com.logistics.corebusiness.waybill.domain.DraftStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA репозиторий для работы с черновиками накладных.
 */
@Repository
public interface WaybillDraftJpaRepository extends JpaRepository<WaybillDraftEntity, Long> {

    Optional<WaybillDraftEntity> findByBarcode(String barcode);

    List<WaybillDraftEntity> findBySenderUserId(Long senderUserId);

    List<WaybillDraftEntity> findByRecipientUserId(Long recipientUserId);

    List<WaybillDraftEntity> findByDraftStatus(DraftStatus status);

    List<WaybillDraftEntity> findByDraftCreatorId(Long creatorId);
}
