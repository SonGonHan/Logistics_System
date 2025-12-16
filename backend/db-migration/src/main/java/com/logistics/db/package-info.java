/**
 * Микросервис инициализации и миграции базы данных.
 *
 * Отвечает за:
 * <ul>
 *   <li>Запуск и управление Flyway миграциями</li>
 *   <li>Инициализацию схем БД при старте приложения</li>
 *   <li>Валидацию целостности БД</li>
 * </ul>
 *
 * Запуск: {@code java -jar db-migration.jar}
 *
 * @see com.logistics.db.DbMigrationApplication
 * @see com.logistics.db.FlywayConfig
 */
package com.logistics.db;