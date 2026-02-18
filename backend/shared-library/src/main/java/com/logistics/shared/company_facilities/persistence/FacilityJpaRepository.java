package com.logistics.shared.company_facilities.persistence;

import com.logistics.shared.company_facilities.domain.FacilityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA репозиторий для сущности {@link FacilityEntity}.
 * 
 * @see FacilityEntity
 */
@Repository
public interface FacilityJpaRepository extends JpaRepository<FacilityEntity, Long> {
    List<FacilityEntity> findAllByType(FacilityType type);
}
