package com.logistics.corebusiness.waybill.adapter.out.persistence.waybill;

import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA репозиторий для работы с накладными.
 */
@Repository
public interface WaybillJpaRepository extends JpaRepository<WaybillEntity, Long> {

    Optional<WaybillEntity> findByWaybillNumber(String waybillNumber);

    List<WaybillEntity> findBySenderUserId(Long senderUserId);

    List<WaybillEntity> findByRecipientUserId(Long recipientUserId);

    List<WaybillEntity> findByStatus(WaybillStatus status);

    List<WaybillEntity> findByWaybillCreatorId(Long creatorId);
}
