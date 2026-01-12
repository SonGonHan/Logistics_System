/**
 * Команды (commands) для inbound ports модуля пользователя.
 *
 * <p>Содержит immutable-объекты (обычно {@code record}), которые передаются в use case слой
 * из входных адаптеров (REST) и описывают входные данные конкретного сценария.
 *
 * <p>Команды не должны содержать зависимостей на web-слой (HTTP, Spring MVC) и должны быть
 * пригодны для использования в тестах use case'ов без поднятия веб-контекста.
 */
package com.logistics.userauth.user.application.port.in.command;
