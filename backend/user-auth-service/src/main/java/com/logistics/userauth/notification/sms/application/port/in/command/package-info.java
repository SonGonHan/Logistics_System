/**
 * Команды (Command objects) для входных use case-ов модуля SMS верификации.
 *
 * <h2>Назначение</h2>
 * Пакет содержит неизменяемые объекты с входными параметрами бизнес-сценариев
 * (отправка кода, проверка кода, проверка rate limit).
 *
 * <h2>Команды</h2>
 * <ul>
 *   <li><b>SendVerificationCodeCommand</b> — входные данные для отправки кода на телефон.</li>
 *   <li><b>VerifyPhoneCommand</b> — телефон и код, введенный пользователем.</li>
 *   <li><b>InternalSmsRateLimiterCommand</b> — телефон/идентификатор для проверки лимитов отправки.</li>
 * </ul>
 *
 * Команды формируются в adapter.in (например, REST-контроллером) и передаются в inbound ports.
 *
 * @see com.logistics.userauth.notification.sms.application.port.in.SendPhoneVerificationCodeUseCase
 * @see com.logistics.userauth.notification.sms.application.port.in.VerifyPhoneUseCase
 * @see com.logistics.userauth.notification.sms.application.port.in.InternalSmsRateLimiterUseCase
 */
package com.logistics.userauth.notification.sms.application.port.in.command;
