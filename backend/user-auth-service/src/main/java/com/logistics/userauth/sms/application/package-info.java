/**
 * Application-слой (Use Cases) модуля SMS верификации телефона.
 *
 * <h2>Назначение</h2>
 * Содержит бизнес-логику сценариев отправки и проверки SMS кодов и определяет контракты
 * взаимодействия с внешней инфраструктурой через порты.
 *
 * <h2>Ключевые подпакеты</h2>
 * <ul>
 *   <li><b>usecase</b> — реализации use case-ов (сервисы сценариев)</li>
 *   <li><b>port</b> — inbound/outbound контракты для адаптеров</li>
 *   <li><b>exception</b> — доменно-ориентированные исключения модуля</li>
 * </ul>
 *
 * Application-слой не должен зависеть от web/DB/Redis деталей; они подключаются через adapter.out.
 *
 * @see com.logistics.userauth.sms.application.usecase
 * @see com.logistics.userauth.sms.application.port
 * @see com.logistics.userauth.sms.application.exception
 */
package com.logistics.userauth.sms.application;
