/**
 * Доменная модель пользователя и связанные перечисления.
 *
 * Классы:
 * <ul>
 *   <li><b>User</b> - Основная доменная модель (идентификатор, контакты, роль, статус)</li>
 *   <li><b>UserRole</b> - Enum ролей (CLIENT, COURIER, DISPATCHER, SYSTEM_ADMIN и т.д.)</li>
 *   <li><b>UserStatus</b> - Enum статусов (ACTIVE, ON_DELETE)</li>
 * </ul>
 *
 * Особенности:
 * <ul>
 *   <li>Независимы от Spring и JPA</li>
 *   <li>Используются во всех слоях приложения</li>
 *   <li>Содержат бизнес-логику и инварианты</li>
 * </ul>
 */
package com.logistics.userauth.user.domain;