/**
 * Модуль email-верификации.
 * Реализует отправку и проверку кодов подтверждения через email.
 *
 * <h2>Архитектура</h2>
 * Следует Clean Architecture / Hexagonal Architecture:
 * <ul>
 *   <li><b>domain/</b> — доменные сущности</li>
 *   <li><b>application/</b> — бизнес-логика, use cases, порты</li>
 *   <li><b>adapter/</b> — адаптеры (REST API, Redis, SMTP)</li>
 * </ul>
 *
 * <h2>Провайдеры</h2>
 * <ul>
 *   <li><b>MockEmailProvider</b> — для разработки (логирует в консоль)</li>
 *   <li><b>SmtpEmailProvider</b> — для продакшена (Spring Mail SMTP)</li>
 * </ul>
 *
 * <h2>Использование</h2>
 * <pre>
 * POST /api/v1/email/send-verification-code - отправка кода
 * POST /api/v1/email/verify-email           - проверка кода
 * GET  /api/v1/email/config                 - конфигурация
 * </pre>
 */
package com.logistics.userauth.notification.email;