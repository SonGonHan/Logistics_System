/**
 * Адаптеры модуля SMS верификации.
 *
 * <h2>Назначение</h2>
 * Содержит входные и выходные адаптеры для интеграции SMS модуля с REST API и инфраструктурой
 * (провайдеры, Redis и т.д.).
 *
 * <h2>Подпакеты</h2>
 * <ul>
 *   <li><b>in</b> — REST контроллеры, DTO и валидация входных данных.</li>
 *   <li><b>out</b> — интеграция с persistence и SMS провайдерами.</li>
 * </ul>
 *
 * @see com.logistics.userauth.sms.adapter.in
 * @see com.logistics.userauth.sms.adapter.out
 */
package com.logistics.userauth.notification.sms.adapter;
