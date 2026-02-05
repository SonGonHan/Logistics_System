# SHARED-LIBRARY: ПОЛНАЯ ДОКУМЕНТАЦИЯ

## Пакет: `com.logistics.shared`

**Описание пакета:**
Общая библиотека, содержащая переиспользуемые компоненты для всех микросервисов системы. 
Включает конфигурацию OpenAPI/Swagger, кастомные валидаторы (телефон, пароль), 
а также сервисы для работы с типами аудит-действий, которые хранятся в shared_data схеме БД.

**ВАЖНО:** Этот пакет – это Spring Boot library, а не standalone приложение. 
Никогда не кладите application.yaml в этот модуль!

---

## Подпакет: `com.logistics.shared.validation`

**Описание подпакета:**
Кастомные аннотации валидации для входных данных. 
Реализует валидацию телефонных номеров и паролей с поддержкой России, Беларуси и Казахстана.

### Аннотация: Phone.java

```java
/**
 * Аннотация для валидации телефонных номеров.
 *
 * <h2>Поддерживаемые форматы</h2>
 * <ul>
 *   <li>Россия: +7XXXXXXXXXX, 7XXXXXXXXXX, +7 (XXX) XXX-XX-XX</li>
 *   <li>Беларусь: +375XXXXXXXXX, 375XXXXXXXXX</li>
 *   <li>Казахстан: +77XXXXXXXXX, 77XXXXXXXXX</li>
 * </ul>
 *
 * <h2>Примеры использования</h2>
 * <pre>
 * \\@Phone
 * private String phone;
 *
 * \\@Phone(message = \"Неверный номер телефона\")
 * String phone;
 * </pre>
 *
 * <h2>Примеры валидных номеров</h2>
 * - 79991234567 (Россия)
 * - +7 (999) 123-45-67 (Россия с форматированием)
 * - +375291234567 (Беларусь)
 * - 77011234567 (Казахстан)
 *
 * <h2>Примеры невалидных номеров</h2>
 * - 123 (слишком короткий)
 * - +1 999 123 4567 (неподдерживаемая страна)
 * - 79991234 (неполный номер)
 *
 * @see PhoneValidator для реализации валидации
 */
@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Phone {
    String message() default \"Неверный формат телефона\";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

### Класс: PhoneValidator.java

```java
/**
 * Валидатор для проверки корректности телефонных номеров.
 *
 * <h2>Алгоритм валидации</h2>
 * <ol>
 *   <li>Проверяет, что значение не null и не пусто</li>
 *   <li>Удаляет пробелы, дефисы и скобки</li>
 *   <li>Сверяет нормализованный номер с регулярным выражением</li>
 *   <li>Возвращает true если совпадает, false в противном случае</li>
 * </ol>
 *
 * <h2>Regex парттерны</h2>
 * - Россия: ^(?:\\\\+7|7)\\\\d{10}$ (11-12 цифр)
 * - Беларусь: ^(?:\\\\+375|375)\\\\d{9}$ (12-13 цифр)
 * - Казахстан: ^(?:\\\\+77|77)\\\\d{9}$ (11-12 цифр)
 *
 * <h2>Примеры</h2>
 * <pre>
 * PhoneValidator validator = new PhoneValidator();
 *
 * validator.isValid(\"79991234567\", null);      // true (Россия)
 * validator.isValid(\"+7 (999) 123-45-67\", null); // true (Россия форматированный)
 * validator.isValid(\"+375291234567\", null);    // true (Беларусь)
 * validator.isValid(\"123\", null);              // false (слишком короткий)
 * validator.isValid(null, null);                // false (null)
 * </pre>
 *
 * @implements ConstraintValidator<Phone, String>
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {
    private static final String REGEX =
        \"^(?:\\\\+7|7)\\\\d{10}$\" +
        \"|^(?:\\\\+375|375)\\\\d{9}$\" +
        \"|^(?:\\\\+77|77)\\\\d{9}$\";

    /**
     * Валидирует телефонный номер.
     *
     * @param value Телефонный номер для валидации
     * @param context Контекст валидации
     * @return true если номер валиден, false в противном случае
     */
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String normalized = value.replaceAll(\"[\\\\s\\\\-()]\", \"\");
        return normalized.matches(REGEX);
    }
}
```

### Аннотация: Password.java

```java
/**
 * Аннотация для валидации сложности пароля.
 *
 * <h2>Требования к паролю</h2>
 * <ul>
 *   <li>Минимум 8 символов</li>
 *   <li>Минимум одна заглавная буква (A-Z)</li>
 *   <li>Минимум одна строчная буква (a-z)</li>
 *   <li>Минимум одна цифра (0-9)</li>
 *   <li>Минимум один спецсимвол (!@#$%^&*)</li>
 * </ul>
 *
 * <h2>Примеры использования</h2>
 * <pre>
 * \\@Password
 * private String password;
 *
 * \\@Password(message = \"Пароль слишком слабый\")
 * String password;
 * </pre>
 *
 * <h2>Примеры валидных паролей</h2>
 * - Password123!
 * - Qwerty1@
 * - MyP@ssw0rd
 * - Admin#2025!
 *
 * <h2>Примеры невалидных паролей</h2>
 * - password123 (нет заглавной и спецсимвола)
 * - Password! (нет цифр)
 * - Pass1! (меньше 8 символов)
 * - PASSWORD123! (нет строчных)
 *
 * @see PasswordValidator для реализации валидации
 */
@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default
        \"Пароль должен содержать минимум 8 символов, \" +
        \"включая цифру, заглавную и строчную букву и спецсимвол\";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

### Класс: PasswordValidator.java

```java
/**
 * Валидатор для проверки сложности паролей.
 *
 * <h2>Реализованные проверки</h2>
 * <ol>
 *   <li>Проверка на null</li>
 *   <li>Проверка длины (минимум 8 символов)</li>
 *   <li>Проверка на наличие заглавной буквы (?=.*[A-Z])</li>
 *   <li>Проверка на наличие строчной буквы (?=.*[a-z])</li>
 *   <li>Проверка на наличие цифры (?=.*\\\\d)</li>
 *   <li>Проверка на наличие спецсимвола (?=.*[^\\\\w\\\\s])</li>
 * </ol>
 *
 * <h2>Regex</h2>
 * ^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[^\\\\w\\\\s]).{8,}$
 *
 * Использует positive lookahead assertions для проверки всех условий.
 *
 * <h2>Примеры</h2>
 * <pre>
 * PasswordValidator validator = new PasswordValidator();
 *
 * validator.isValid(\"Password123!\", null);  // true
 * validator.isValid(\"password123!\", null);  // false (нет заглавной)
 * validator.isValid(\"Password123\", null);   // false (нет спецсимвола)
 * validator.isValid(\"Pass1!\", null);        // false (меньше 8 символов)
 * validator.isValid(null, null);             // false
 * </pre>
 *
 * @implements ConstraintValidator<Password, String>
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {
    private static final String REGEX =
        \"^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[^\\\\w\\\\s]).{8,}$\";

    /**
     * Валидирует пароль по критериям сложности.
     *
     * @param value Пароль для валидации
     * @param context Контекст валидации
     * @return true если пароль достаточно сложный, false иначе
     */
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.matches(REGEX);
    }
}
```

---

## Подпакет: `com.logistics.shared.audit_action`

**Описание подпакета:**
Доменная логика и сервисы для работы с типами аудит-действий.
Эти типы используются по всей системе для логирования пользовательских действий.

### Подпакет: `com.logistics.shared.audit_action.domain`

**Описание:** Содержит доменную модель (бизнес-логика, не зависит от фреймворков и БД).

#### Класс: AuditActionType.java

```java
/**
 * Доменная сущность для типа аудит-действия.
 *
 * <h2>Назначение</h2>
 * Описывает категорию действия, которое можно залогировать в системе:
 * - USER_LOGIN (категория: AUTHENTICATION)
 * - ORDER_CREATED (категория: ORDER_MANAGEMENT)
 * - PROFILE_UPDATED (категория: USER_MANAGEMENT)
 *
 * <h2>Структура</h2>
 * - id: короткий уникальный идентификатор типа
 * - actionName: машиночитаемое имя (USER_LOGIN, ORDER_CREATED)
 * - category: категория для группировки (AUTHENTICATION, ORDER_MANAGEMENT)
 * - description: читаемое описание на русском
 *
 * <h2>Примеры</h2>
 * <pre>
 * AuditActionType userLogin = AuditActionType.builder()
 *   .id((short) 1)
 *   .actionName(\"USER_LOGIN\")
 *   .category(\"AUTHENTICATION\")
 *   .description(\"Пользователь вошел в систему\")
 *   .build();
 * </pre>
 *
 * @author Logistics Team
 * @version 1.0.0
 * @since 2025-12-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditActionType {
    private short id;
    private String actionName;
    private String category;
    private String description;
}
```

### Подпакет: `com.logistics.shared.audit_action.persistence`

#### Класс: AuditActionTypeEntity.java

```java
/**
 * JPA сущность для хранения типов аудит-действий в БД.
 *
 * <h2>Таблица в БД</h2>
 * Schema: shared_data
 * Table: audit_action_types
 *
 * @see AuditActionJpaRepository для работы с БД
 * @see AuditActionTypeMapper для преобразования Domain ↔ Entity
 */
@Entity
@Table(name = \"audit_action_types\", schema = \"shared_data\")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditActionTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = \"audit_action_type_seq\")
    @SequenceGenerator(
        name = \"audit_action_type_seq\",
        sequenceName = \"audit_action_types_action_type_id_seq\",
        schema = \"shared_data\",
        allocationSize = 1
    )
    @Column(name = \"action_type_id\")
    private Short id;

    @Column(name = \"action_name\", nullable = false)
    private String actionName;

    @Column(name = \"category\", nullable = false)
    private String category;

    @Column(name = \"description\")
    private String description;
}
```

#### Интерфейс: AuditActionJpaRepository.java

```java
/**
 * Spring Data JPA репозиторий для работы с типами аудит-действий.
 *
 * <h2>Примеры использования</h2>
 * <pre>
 * Optional<AuditActionTypeEntity> entity = repo.findById(1);
 * Optional<AuditActionTypeEntity> entity = repo.findByActionName(\"USER_LOGIN\");
 * </pre>
 *
 * @see AuditActionTypeEntity для сущности
 * @see AuditActionTypeService для бизнес-логики
 */
@Repository
public interface AuditActionJpaRepository extends JpaRepository<AuditActionTypeEntity, Integer> {
    Optional<AuditActionTypeEntity> findByCategory(String category);
    Optional<AuditActionTypeEntity> findByActionName(String actionName);
}
```

#### Класс: AuditActionTypeMapper.java

```java
/**
 * MapStruct-подобный маппер для преобразования между Domain и Entity.
 *
 * <h2>Назначение</h2>
 * Конвертирует между двумя представлениями одного объекта:
 * - AuditActionType (доменная модель, не привязана к БД)
 * - AuditActionTypeEntity (JPA entity, привязана к БД)
 *
 * @see AuditActionType для доменной модели
 * @see AuditActionTypeEntity для JPA entity
 */
@Component
public class AuditActionTypeMapper {
    public AuditActionType toDomain(AuditActionTypeEntity entity) {
        return AuditActionType.builder()
            .id(entity.getId())
            .actionName(entity.getActionName())
            .category(entity.getCategory())
            .description(entity.getDescription())
            .build();
    }

    public AuditActionTypeEntity toEntity(AuditActionType domain) {
        return AuditActionTypeEntity.builder()
            .id(domain.getId())
            .actionName(domain.getActionName())
            .category(domain.getCategory())
            .description(domain.getDescription())
            .build();
    }
}
```

#### Класс: AuditActionTypeService.java

```java
/**
 * Бизнес-сервис для работы с типами аудит-действий.
 *
 * <h2>Ответственность</h2>
 * Предоставляет различные способы поиска типов аудит-действий:
 * - По ID
 * - По имени действия (actionName)
 * - По категории
 *
 * @see AuditActionJpaRepository для работы с БД
 * @see AuditActionTypeMapper для преобразования Entity ↔ Domain
 */
@Service
@RequiredArgsConstructor
public class AuditActionTypeService {
    private final AuditActionJpaRepository repo;
    private final AuditActionTypeMapper mapper;

    public Optional<AuditActionType> getActionTypeById(Integer id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    public Optional<AuditActionType> getActionTypeActionName(String actionName) {
        return repo.findByActionName(actionName).map(mapper::toDomain);
    }

    public Optional<AuditActionType> getActionTypeByCategory(String category) {
        return repo.findByCategory(category).map(mapper::toDomain);
    }
}
```

---

## Класс: SharedLibraryConfiguration.java

```java
/**
 * Конфигурация Spring для shared-library модуля.
 *
 * <h2>Назначение</h2>
 * Регистрирует бины и конфигурирует компоненты shared-library,
 * которые будут использоваться другими микросервисами.
 *
 * <h2>Использование в других сервисах</h2>
 * <pre>
 * \\@SpringBootApplication
 * \\@ComponentScan(basePackages = {
 *     \"com.myservice\",
 *     \"com.logistics.shared\"
 * })
 * \\@EnableJpaRepositories(basePackages = \"com.logistics.shared\")
 * public class MyServiceApplication { }
 * </pre>
 *
 * @author Logistics Team
 * @version 1.0.0
 * @since 2025-12-15
 */
@Configuration
@EntityScan(\"com.logistics.shared\")
@EnableJpaRepositories(\"com.logistics.shared\")
public class SharedLibraryConfiguration {
}
```

---

## Класс: OpenApiConfig.java

```java
/**
 * Конфигурация OpenAPI/Swagger для всех микросервисов.
 *
 * <h2>Назначение</h2>
 * Предоставляет единую конфигурацию документации API для всех сервисов.
 * Каждый микросервис импортирует эту конфигурацию через @Import,
 * чтобы иметь единообразный вид Swagger UI.
 *
 * <h2>Использование</h2>
 * <pre>
 * \\@SpringBootApplication
 * \\@Import(OpenApiConfig.class)
 * public class UserAuthServiceApplication { }
 * </pre>
 *
 * После импорта Swagger UI будет доступен по:
 * http://localhost:8080/api/v1/swagger-ui.html
 *
 * @author Logistics Team
 * @version 1.0.0
 * @since 2025-12-15
 */
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title(\"Logistics System API\")
                .version(\"1.0.0\")
                .description(\"REST API для системы логистики и управления доставкой\")
                .contact(new Contact()
                    .name(\"Logistics Development Team\")
                    .email(\"dev@logistics.com\")
                    .url(\"https://logistics.com\")))
            .addSecurityItem(new SecurityRequirement().addList(\"Bearer JWT\"));
    }
}
```
