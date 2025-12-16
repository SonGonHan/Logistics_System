package com.logistics.userauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
/**
 * Точка входа микросервиса аутентификации.
 *
 * <h2>Ответственность</h2>
 * - Инициализирует Spring Boot приложение
 * - Подключает shared-library компоненты
 * - Импортирует OpenAPI конфигурацию
 * - Сканирует все @Component, @Service, @Controller в com.logistics.userauth
 *
 * <h2>Архитектура</h2>
 * Используется Clean Architecture (Hexagonal Architecture):
 * <pre>
 * adapter.in      - REST контроллеры, маппреры DTO
 * adapter.out     - JPA entities, репозитории, маппреры персистенса
 * application     - Use Cases, порты, команды
 * domain          - Доменные сущности (User, UserRole и т.д.)
 * infrastructure  - Spring Security, фильтры, конфигурация
 * common          - Глобальные exception handlers, утилиты
 * </pre>
 *
 * <h2>Конфигурация</h2>
 * Читается из application.yml:
 * - server.port: 8080 (или как настроено)
 * - server.servlet.context-path: /api/v1
 * - Credentials для БД (PostgreSQL)
 * - JWT конфигурация (secret, expiration)
 *
 * @author Logistics Team
 * @version 1.0.0
 * @since 2025-12-15
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.logistics.userauth",
        "com.logistics.shared"
})
@EnableJpaRepositories(basePackages = "com.logistics.userauth.**.persistence")
@EntityScan(basePackages = "com.logistics.userauth.**.persistence")
public class UserAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserAuthServiceApplication.class, args);
    }

}
