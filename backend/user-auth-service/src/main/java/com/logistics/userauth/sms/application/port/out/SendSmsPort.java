package com.logistics.userauth.sms.application.port.out;

/**
 * Порт для отправки SMS через внешний провайдер.
 */
public interface SendSmsPort {
    boolean sendVerificationCode(String phone, String code);
}
