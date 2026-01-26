/**
 * Persistence-адаптер (adapter.out) модуля SMS верификации.
 *
 * <h2>Назначение</h2>
 * Реализует хранение/получение SMS кодов и статуса верификации телефона во внешнем хранилище
 * (например, Redis) и предоставляет реализацию выходного порта {@link com.logistics.userauth.notification.sms.application.port.out.SmsRepository}.
 *
 * <h2>Состав</h2>
 * <ul>
 *   <li><b>SmsRepositoryPersistenceAdapter</b> — реализация порта SmsRepository.</li>
 *   <li><b>RedisSmsRepository</b> — низкоуровневые операции с Redis (ключи, TTL, счетчики попыток).</li>
 * </ul>
 *
 * @see com.logistics.userauth.notification.sms.application.port.out.SmsRepository
 * @see com.logistics.userauth.notification.sms.adapter.out.persistence.SmsPersistenceAdapter
 * @see com.logistics.userauth.notification.sms.adapter.out.persistence.RedisSmsRepository
 */
package com.logistics.userauth.notification.sms.adapter.out.persistence;
