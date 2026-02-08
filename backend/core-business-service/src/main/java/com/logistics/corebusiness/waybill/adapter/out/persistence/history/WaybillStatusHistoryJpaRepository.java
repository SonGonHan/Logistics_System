package com.logistics.corebusiness.waybill.adapter.out.persistence.history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA репозиторий для работы с историей статусов накладных.
 */
@Repository
public interface WaybillStatusHistoryJpaRepository extends JpaRepository<WaybillStatusHistoryEntity, Long> {

    List<WaybillStatusHistoryEntity> findByWaybillIdOrderByChangedAtAsc(Long waybillId);

    List<WaybillStatusHistoryEntity> findByFacilityId(Long facilityId);

    List<WaybillStatusHistoryEntity> findByChangedBy(Long userId);
}
