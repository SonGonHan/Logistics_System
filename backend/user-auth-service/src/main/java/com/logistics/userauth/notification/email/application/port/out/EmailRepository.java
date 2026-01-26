package com.logistics.userauth.notification.email.application.port.out;

import com.logistics.userauth.notification.common.application.port.out.VerificationCodeRepository;
import com.logistics.userauth.notification.email.domain.EmailVerificationCode;

import java.util.Optional;

/**
 * Выходной порт для работы с хранилищем кодов верификации email.
 * Реализуется Redis адаптером.
 */
public interface EmailRepository extends VerificationCodeRepository<EmailVerificationCode> {

}