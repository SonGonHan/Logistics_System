/**
 * Доменная модель записи аудита.
 *
 * Описывает действие пользователя в системе:
 * <ul>
 *   <li><b>user</b> - Кто выполнил действие</li>
 *   <li><b>actionType</b> - Какое действие (от shared-library)</li>
 *   <li><b>tableName</b> - В какой таблице произошло</li>
 *   <li><b>recordId</b> - Какую запись коснулось</li>
 *   <li><b>newValues</b> - JSON с новыми значениями</li>
 *   <li><b>performedAt</b> - Когда произошло</li>
 *   <li><b>ipAddress</b> - С какого IP</li>
 * </ul>
 */
package com.logistics.userauth.audit.domain;