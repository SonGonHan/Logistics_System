/**
 * Порты (интерфейсы) прикладного уровня для JWT-аутентификации.
 *
 * Определяет границы приложения по паттерну Hexagonal Architecture:
 * <ul>
 *   <li><b>port.in</b> - Входные порты (use cases). Описывают, что может делать приложение.
 *       Реализуются в пакете usecase. Вызываются адаптерами (web, message queues).</li>
 *   <li><b>port.out</b> - Выходные порты. Описывают, как приложение общается с внешним миром
 *       (БД, внешние API, генерация токенов). Реализуются в адаптерах (persistence, out).</li>
 * </ul>
 *
 * Преимущества:
 * <ul>
 *   <li>Независимость от фреймворков</li>
 *   <li>Тестируемость (mock-адаптеры)</li>
 *   <li>Явные контракты между слоями</li>
 *   <li>Гибкость (легко менять реализации)</li>
 * </ul>
 *
 * @see <a href="https://alistair.cockburn.us/hexagonal-architecture/">Hexagonal Architecture</a>
 */
package com.logistics.userauth.auth.jwt.application.port;