package com.logistics.corebusiness.rating.application.port.out;

import com.logistics.corebusiness.rating.domain.Rating;

import java.util.List;
import java.util.Optional;

/**
 * Порт (интерфейс) репозитория рейтингов.
 *
 * <h2>Назначение</h2>
 * Определяет контракт доступа к данным рейтингов.
 * Реализация - {@link com.logistics.corebusiness.rating.adapter.out.persistence.RatingPersistenceAdapter}.
 *
 * <h2>Важно</h2>
 * Интерфейс оперирует только доменными типами, без JPA зависимостей.
 */
public interface RatingRepository {

    Rating save(Rating rating);

    Optional<Rating> findById(Long id);

    Optional<Rating> findByWaybillId(Long waybillId);

    List<Rating> findByRatingCreatorId(Long userId);

    boolean existsByWaybillId(Long waybillId);
}
