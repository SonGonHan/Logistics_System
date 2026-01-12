/**
 * Адаптеры SMS провайдеров.
 *
 * <h2>Назначение</h2>
 * Реализации {@link com.logistics.userauth.sms.application.port.out.SendSmsPort},
 * которые отвечают за фактическую отправку SMS (внешний API или mock для разработки).
 *
 * <h2>Реализации</h2>
 * <ul>
 *   <li><b>MockSmsProvider</b> — используется по умолчанию в режиме разработки (логирует код).</li>
 *   <li><b>SmscSmsProvider</b> — интеграция с внешним SMS API.</li>
 * </ul>
 *
 * @see com.logistics.userauth.sms.adapter.out.provider.MockSmsProvider
 * @see com.logistics.userauth.sms.adapter.out.provider.SmscSmsProvider
 * @see com.logistics.userauth.sms.application.port.out.SendSmsPort
 */
package com.logistics.userauth.sms.adapter.out.provider;
