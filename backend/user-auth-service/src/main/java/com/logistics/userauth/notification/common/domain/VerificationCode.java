package com.logistics.userauth.notification.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Абстрактная доменная модель для кода верификации.
 *
 * <p>Содержит общие поля и логику для всех типов верификационных кодов.</p>
 *
 * <h2>Общие характеристики</h2>
 * <ul>
 *   <li>Код - временная строка для подтверждения</li>
 *   <li>Срок действия - время истечения кода</li>
 *   <li>Попытки - счётчик неудачных попыток ввода</li>
 * </ul>
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class VerificationCode {

    private String id;

    private String code;

    private LocalDateTime expiresAt;

    private int attempts;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Получить идентификатор получателя (для переопределения в подклассах с удобными названиями).
     *
     * @return идентификатор получателя
     */
    public String getId() {
        return id;
    }
}