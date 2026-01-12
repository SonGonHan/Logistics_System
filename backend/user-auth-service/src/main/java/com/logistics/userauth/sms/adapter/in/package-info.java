/**
 * Входящие адаптеры (Inbound Adapters) модуля SMS верификации.
 *
 * <h2>Назначение</h2>
 * REST слой, принимающий запросы на отправку кода и верификацию телефона.
 * Преобразует HTTP DTO в команды use case-слоя.
 *
 * <h2>Подпакеты</h2>
 * <ul>
 *   <li><b>web</b> — REST контроллеры.</li>
 *   <li><b>web.dto</b> — DTO запросов/ответов.</li>
 *   <li><b>validation</b> — валидация SMS-кода (например, {@code @SmsCode}).</li>
 * </ul>
 *
 * @see com.logistics.userauth.sms.application.port.in.SendVerificationCodeUseCase
 * @see com.logistics.userauth.sms.application.port.in.VerifyPhoneUseCase
 */
package com.logistics.userauth.sms.adapter.in;
