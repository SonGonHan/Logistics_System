package com.logistics.corebusiness.rating.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA репозиторий для RatingEntity.
 */
@Repository
public interface RatingJpaRepository extends JpaRepository<RatingEntity, Long> {

    Optional<RatingEntity> findByWaybillId(Long waybillId);

    List<RatingEntity> findByUserId(Long userId);

    boolean existsByWaybillId(Long waybillId);
}
