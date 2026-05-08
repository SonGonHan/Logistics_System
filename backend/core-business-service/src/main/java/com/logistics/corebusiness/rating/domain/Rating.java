package com.logistics.corebusiness.rating.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Доменный объект рейтинга (оценки) доставки.
 *
 * <h2>Назначение</h2>
 * Представляет оценку и отзыв пользователя для доставленной накладной.
 *
 * <h2>Бизнес-правила</h2>
 * <ul>
 *   <li>Оценка (score) от 1 до 5</li>
 *   <li>Отзыв (comment) опционален</li>
 *   <li>Одна оценка на одну накладную от одного пользователя</li>
 *   <li>Оценить можно только накладную со статусом DELIVERED</li>
 * </ul>
 *
 * @see com.logistics.corebusiness.waybill.domain.Waybill связанная накладная
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rating {
    private Long id;
    private Long waybillId;
    private Long ratingCreatorId;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;
}
