/**
 * Общие security утилиты для работы с JWT токенами.
 *
 * <h2>Содержимое пакета</h2>
 * <ul>
 *   <li>{@link com.logistics.shared.security.SecurityUtils} - извлечение данных из JWT токенов</li>
 * </ul>
 *
 * <h2>Назначение</h2>
 * Обеспечивает единообразную работу с JWT токенами во всех микросервисах системы.
 * Все сервисы должны использовать эти утилиты для извлечения userId, role и других
 * claims из JWT токенов.
 *
 * <h2>Spring Security 7</h2>
 * Код использует актуальные API Spring Security 7 для работы с OAuth2 Resource Server
 * и JWT токенами.
 */
package com.logistics.shared.security;
