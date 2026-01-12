/**
 * Порты (Ports) модуля SMS верификации — контракты взаимодействия application-слоя с адаптерами.
 *
 * <h2>Назначение</h2>
 * Пакет группирует интерфейсы, которые описывают границы приложения:
 * что модуль предоставляет внешнему миру (inbound ports) и что требует от инфраструктуры (outbound ports).
 *
 * <h2>Структура</h2>
 * <ul>
 *   <li><b>port.in</b> — входные порты (use case интерфейсы), вызываемые из adapter.in</li>
 *   <li><b>port.out</b> — выходные порты (контракты инфраструктуры), реализуемые adapter.out</li>
 * </ul>
 *
 * @see com.logistics.userauth.sms.application.port.in
 * @see com.logistics.userauth.sms.application.port.out
 */
package com.logistics.userauth.sms.application.port;
