package com.logistics.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(
		exclude = HibernateJpaAutoConfiguration.class
)
public class DbMigrationApplication {
	private static final Logger log = LoggerFactory.getLogger(DbMigrationApplication.class);

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
