package com.logistics.db;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Конфигурация Flyway для управления миграциями БД.
 *
 * <h2>Ответственность</h2>
 * Создает и настраивает бин Flyway, который:
 * - Сканирует миграции из classpath:db/migration
 * - Применяет новые миграции на старте приложения
 * - Валидирует целостность схемы
 * - Предотвращает случайное удаление БД (cleanDisabled: true)
 *
 * <h2>Параметры Flyway</h2>
 * <table border="1">
 *   <tr><th>Параметр</th><th>Значение</th><th>Описание</th></tr>
 *   <tr>
 *     <td>locations</td>
 *     <td>classpath:db/migration</td>
 *     <td>Папка с SQL файлами миграций</td>
 *   </tr>
 *   <tr>
 *     <td>baselineOnMigrate</td>
 *     <td>true</td>
 *     <td>Создать baseline для уже существующей БД</td>
 *   </tr>
 *   <tr>
 *     <td>validateOnMigrate</td>
 *     <td>true</td>
 *     <td>Проверить целостность применённых миграций</td>
 *   </tr>
 *   <tr>
 *     <td>cleanDisabled</td>
 *     <td>true</td>
 *     <td>КРИТИЧНО: защита от удаления БД в production</td>
 *   </tr>
 * </table>
 *
 * <h2>Пример SQL миграции</h2>
 * <pre>
 * File: V1__Create_users_table.sql
 *
 * CREATE SCHEMA IF NOT EXISTS user_management;
 *
 * CREATE TABLE user_management.users (
 *     user_id BIGSERIAL PRIMARY KEY,
 *     phone VARCHAR(20) UNIQUE NOT NULL,
 *     email VARCHAR(255),
 *     password_hash VARCHAR(255),
 *     first_name VARCHAR(100),
 *     last_name VARCHAR(100),
 *     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
 * );
 * </pre>
 *
 * <h2>Условное выполнение</h2>
 * Конфигурация применяется только если:
 * app.db-migration.enabled = true (или отсутствует - применяется по умолчанию)
 *
 * @see DbMigrationApplication для точки входа
 * @author Logistics Team
 * @version 1.0.0
 * @since 2025-12-15
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnProperty(
        name = "app.db-migration.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class FlywayConfig {

    /**
     * Создает и настраивает Flyway бин.
     *
     * @param dataSource Источник данных для подключения к БД
     * @return Настроенный и выполненный Flyway объект
     */
    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .validateOnMigrate(true)
                .cleanDisabled(true)  // Отключить очистку в production
                .load();

        flyway.migrate();
        return flyway;
    }
}
