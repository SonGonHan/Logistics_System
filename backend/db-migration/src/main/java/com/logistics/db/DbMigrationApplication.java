package com.logistics.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * Точка входа приложения для инициализации и миграции базы данных.
 *
 * <h2>Назначение</h2>
 * <p>
 * Это специализированное Spring Boot приложение, которое:
 * </p>
 * <ul>
 *   <li>Запускается один раз при инициализации системы</li>
 *   <li>Применяет все ожидающие миграции Flyway</li>
 *   <li>Проверяет целостность схемы БД</li>
 *   <li>Логирует ход выполнения миграций</li>
 *   <li>Завершается после успешного выполнения миграций</li>
 * </ul>
 *
 * <h2>Процесс запуска</h2>
 * <ol>
 *   <li>Spring Boot загружает контекст</li>
 *   <li>Flyway сканирует папку classpath:db/migration</li>
 *   <li>Применяются все новые миграции в порядке версионирования</li>
 *   <li>В логах отображается статус выполнения</li>
 *   <li>Приложение завершает работу с кодом 0 (успех) или 1 (ошибка)</li>
 * </ol>
 *
 * <h2>Использование</h2>
 * <pre>
 * Запуск из Docker Compose:
 * docker-compose run --rm db-migration
 *
 * Или напрямую:
 * java -jar db-migration-1.0.0.jar
 * </pre>
 *
 * <h2>Структура файлов миграций</h2>
 * <pre>
 * backend/db-migration/src/main/resources/db/migration/
 * ├── V1__Initial_schema.sql
 * ├── V2__Add_users_table.sql
 * └── V3__Add_indexes.sql
 * </pre>
 *
 * <h2>Конфигурация</h2>
 * Читается из application.yml:
 * - spring.datasource.url - адрес БД
 * - spring.datasource.username - пользователь БД
 * - spring.datasource.password - пароль БД
 * - app.db-migration.enabled - включить/отключить миграции
 *
 * @see FlywayConfig для конфигурации Flyway
 * @author Logistics Team
 * @version 1.0.0
 * @since 2025-12-15
 */
@SpringBootApplication(
		exclude = HibernateJpaAutoConfiguration.class
)
public class DbMigrationApplication {
	private static final Logger log = LoggerFactory.getLogger(DbMigrationApplication.class);
	/**
	 * Главный метод приложения.
	 *
	 * Логирует начало и завершение миграций, обрабатывает ошибки.
	 *
	 * @param args Аргументы командной строки (игнорируются)
	 */
	public static void main(String[] args) {
		try {
			log.info("========== Начало инициализации БД ==========");
			ConfigurableApplicationContext context =
					SpringApplication.run(DbMigrationApplication.class, args);

			log.info("✓ Миграции БД выполнены успешно");
			log.info("========== Завершение работы миграций ==========");

			// Закрыть контекст и завершить приложение
			context.close();
			System.exit(0);

		} catch (Exception e) {
			log.error("✗ Ошибка при выполнении миграций БД", e);
			System.exit(1);
		}
	}

}
