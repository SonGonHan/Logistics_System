package com.logistics.corebusiness.rating.adapter.out.persistence;

import com.logistics.corebusiness.rating.application.port.out.RatingRepository;
import com.logistics.corebusiness.rating.domain.Rating;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Адаптер персистенции для рейтингов.
 *
 * <h2>Назначение</h2>
 * Реализует {@link RatingRepository} и делегирует операции
 * в {@link RatingJpaRepository} с конвертацией через {@link RatingPersistenceMapper}.
 */
@Component
@RequiredArgsConstructor
public class RatingPersistenceAdapter implements RatingRepository {

    private final RatingJpaRepository jpaRepository;
    private final RatingPersistenceMapper mapper;

    @Override
    public Rating save(Rating rating) {
        var entity = mapper.toEntity(rating);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Rating> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Rating> findByWaybillId(Long waybillId) {
        return jpaRepository.findByWaybillId(waybillId).map(mapper::toDomain);
    }

    @Override
    public List<Rating> findByRatingCreatorId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByWaybillId(Long waybillId) {
        return jpaRepository.existsByWaybillId(waybillId);
    }
}
