package com.logistics.userauth.notification.email.domain;

import com.logistics.userauth.notification.common.domain.VerificationCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Доменная сущность кода верификации email.
 *
 * <p>Представляет временный код подтверждения, отправленный на email пользователя.</p>
 * <p>Наследует общую логику от {@link VerificationCode}.</p>
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmailVerificationCode extends VerificationCode {

    public String getEmail() {
        return getId();
    }

    public void setEmail(String email) {
        setId(email);
    }
}
