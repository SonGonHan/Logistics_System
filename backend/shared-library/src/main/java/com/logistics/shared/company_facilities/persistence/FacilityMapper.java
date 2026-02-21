package com.logistics.shared.company_facilities.persistence;

import com.logistics.shared.company_facilities.domain.Facility;
import org.springframework.stereotype.Component;

/**
 * Маппер между {@link FacilityEntity} (JPA-слой) и {@link Facility} (доменный объект).
 *
 * <p>Изолирует доменный слой от деталей персистентности: ни {@link Facility},
 * ни его пользователи не зависят от JPA-аннотаций или Spring Data.
 *
 * @see FacilityEntity
 * @see Facility
 */
@Component
public class FacilityMapper {

  public Facility toDomain(FacilityEntity entity) {
        return Facility.builder()
                .id(entity.getId())
                .type(entity.getType())
                .name(entity.getName())
                .address(entity.getAddress())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .createdAt(entity.getCreatedAt())
                .closedDate(entity.getClosedDate())
                .build();
    }

    public FacilityEntity toEntity(Facility domain) {
        return FacilityEntity.builder()
                .id(domain.getId())
                .type(domain.getType())
                .name(domain.getName())
                .address(domain.getAddress())
                .latitude(domain.getLatitude())
                .longitude(domain.getLongitude())
                .createdAt(domain.getCreatedAt())
                .closedDate(domain.getClosedDate())
                .build();
    }
}