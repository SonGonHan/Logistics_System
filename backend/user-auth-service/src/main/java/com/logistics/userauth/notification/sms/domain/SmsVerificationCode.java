package com.logistics.userauth.notification.sms.domain;

import com.logistics.userauth.notification.common.domain.VerificationCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Доменная модель для SMS кода верификации.
 *
 * <p>Наследует общую логику от {@link VerificationCode}.</p>
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SmsVerificationCode extends VerificationCode {

    public String getPhone() {
        return getId();
    }

    public void setPhone(String phone) {
        setId(phone);
    }
}
