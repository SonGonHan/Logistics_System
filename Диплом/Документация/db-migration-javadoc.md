# DB-MIGRATION: ПОЛНАЯ ДОКУМЕНТАЦИЯ

## Пакет: `com.logistics.db`

**Описание пакета:**
Модуль инициализации и управления миграциями базы данных системы логистики. 
Использует Flyway для версионирования схемы БД и автоматического применения миграций при старте приложения. Гарантирует, что все микросервисы работают с одной и той же версией БД с согласованной схемой.

---

## Класс: DbMigrationApplication.java

```java
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
@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
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

            context.close();
            System.exit(0);
        } catch (Exception e) {
            log.error("✗ Ошибка при выполнении миграций БД", e);
            System.exit(1);
        }
    }
}
```

---

## Класс: FlywayConfig.java

```java
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
```

---

## Рекомендации по использованию

### Добавление новой миграции

1. Создайте файл в `src/main/resources/db/migration/`
2. Используйте формат имени: `V<NUMBER>__<DESCRIPTION>.sql`
   - Пример: `V4__Add_audit_logs_table.sql`
3. Напишите SQL скрипт
4. При следующем запуске приложения миграция будет автоматически применена

### Правила написания миграций

- Используйте `IF NOT EXISTS` для создания таблиц (идемпотентность)
- Всегда указывайте schema
- Добавляйте комментарии в SQL для неочевидных операций
- Тестируйте миграции на локальной БД перед коммитом
- НИКОГДА не удаляйте старые миграции, даже если они неправильные
  - Для исправления создавайте новую миграцию

### Откат миграции (если необходимо)

```sql
-- File: V99__Rollback_previous_changes.sql
-- Это не откатывает версию, а просто отменяет изменения новой миграцией
ALTER TABLE users DROP COLUMN new_column;
```
