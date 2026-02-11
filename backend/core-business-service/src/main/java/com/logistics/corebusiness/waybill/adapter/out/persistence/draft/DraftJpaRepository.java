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
public interface DraftJpaRepository extends JpaRepository<DraftEntity, Long> {

    Optional<DraftEntity> findByBarcode(String barcode);

    List<DraftEntity> findBySenderUserId(Long senderUserId);

    List<DraftEntity> findByRecipientUserId(Long recipientUserId);

    List<DraftEntity> findByDraftStatus(DraftStatus status);

    List<DraftEntity> findByDraftCreatorId(Long creatorId);
}
