package com.logistics.shared;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("com.logistics.shared")
@EnableJpaRepositories("com.logistics.shared")
public class SharedLibraryConfiguration {

}package com.logistics.shared.audit_action.persistence;

import com.logistics.shared.audit_action.domain.AuditActionType;
import org.springframework.stereotype.Component;

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
package com.logistics.shared.audit_action.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuditActionJpaRepository extends JpaRepository<AuditActionTypeEntity, Integer> {

    Optional<AuditActionTypeEntity> findByCategory(String category);

    Optional<AuditActionTypeEntity> findByActionName(String actionName);
}
package com.logistics.shared.audit_action.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "audit_action_types",
        schema = "shared_data",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "action_name")
        },
        indexes = {
                @Index(columnList = "user_id", name = "idx_user_sessions_user_id"),
                @Index(columnList = "expires_at", name = "idx_user_sessions_expires_at")
        }
)
public class AuditActionTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_type_id")
    private Short id;

    @Column(name = "action_name", nullable = false)
    private String actionName;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "description")
    private String description;

}
package com.logistics.shared.audit_action.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
package com.logistics.shared.audit_action;

import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.shared.audit_action.persistence.AuditActionJpaRepository;
import com.logistics.shared.audit_action.persistence.AuditActionTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

}package com.logistics.shared;

//@SpringBootTest
class SharedLibraryApplicationTests {

//    @Test
    void contextLoads() {
    }

}
package com.logistics.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
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
package com.logistics.db;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties
@ConditionalOnProperty(
        name = "app.db-migration.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class FlywayConfig {

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
package com.logistics.db;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
class DbMigrationApplicationTests {

	@Test
	void contextLoads() {
	}

}
package com.logistics.userauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.modulith.Modulith;

@Modulith
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
package com.logistics.userauth.audit.app.out;


import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.userauth.audit.domain.AuditLog;
import com.logistics.userauth.user.domain.User;

import java.util.Optional;

public interface AuditLogRepository {
    void save(AuditLog auditLog);

    void delete(AuditLog auditLog);

    Optional<AuditLog> findByUser(User user);

    Optional<AuditLog> findByActionType(AuditActionType actionType);

    Optional<AuditLog> findByActorIdentifier(String actorIdentifier);
}
package com.logistics.userauth.audit.adapters.in;

import com.logistics.userauth.audit.adapters.in.dto.AuditActionTypeDTO;
import com.logistics.userauth.audit.adapters.in.dto.AuditLogDTO;
import com.logistics.userauth.audit.domain.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogControllerMapper {

    private final AuditActionTypeControllerMapper actionTypeMapper;

    public AuditLogDTO toDTO(AuditLog domain) {
        return AuditLogDTO.builder()
                .user(domain.getUser())
                .actionTypeDTO(actionTypeMapper.toDTO(domain.getActionType()))
                .tableName(domain.getTableName())
                .recordId(domain.getRecordId())
                .actorIdentifier(domain.getActorIdentifier())
                .performedAt(domain.getPerformedAt())
                .newValues(domain.getNewValues())
                .build();
    }

    public AuditLog toDomain(AuditLogDTO dto) {
        return AuditLog.builder()
                .user(dto.getUser())
                .actionType(actionTypeMapper.toDomain(dto.getActionTypeDTO()))
                .tableName(dto.getTableName())
                .recordId(dto.getRecordId())
                .actorIdentifier(dto.getActorIdentifier())
                .performedAt(dto.getPerformedAt())
                .newValues(dto.getNewValues())
                .build();
    }
}
package com.logistics.userauth.audit.adapters.in;

import com.logistics.shared.audit_action.AuditActionTypeService;
import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.userauth.audit.adapters.in.dto.AuditActionTypeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditActionTypeControllerMapper {

    private final AuditActionTypeService service;

    public AuditActionTypeDTO toDTO(AuditActionType domain) {
        return AuditActionTypeDTO.builder()
                .actionName(domain.getActionName())
                .description(domain.getDescription())
                .category(domain.getCategory())
                .build();
    }

    public AuditActionType toDomain(AuditActionTypeDTO dto) {
        return AuditActionType.builder()
                .id(service.getActionTypeActionName(dto.getActionName()).get().getId())
                .actionName(dto.getActionName())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .build();
    }
}
package com.logistics.userauth.audit.adapters.in.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditActionTypeDTO {
    private String actionName;
    private String category;
    private String description;
}
package com.logistics.userauth.audit.adapters.in.dto;

import com.logistics.userauth.user.domain.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;


@Data
@Builder
public class AuditLogDTO {

    private User user;

    private AuditActionTypeDTO actionTypeDTO;

    private String tableName;

    private long recordId;

    private String actorIdentifier;

    private Map<String, Object> newValues;

    private LocalDateTime performedAt;
}
package com.logistics.userauth.audit.adapters.out.persistence;

import com.logistics.shared.audit_action.persistence.AuditActionTypeEntity;
import com.logistics.userauth.user.adapters.out.persistence.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface AuditLogJpaRepository extends JpaRepository<AuditLogEntity, Long> {

    Optional<AuditLogEntity> findByUser(UserEntity user);

    Optional<AuditLogEntity> findByActionType(AuditActionTypeEntity actionType);

    Optional<AuditLogEntity> findByActorIdentifier(String actorIdentifier);

}
package com.logistics.userauth.audit.adapters.out.persistence;

import com.logistics.shared.audit_action.persistence.AuditActionTypeMapper;
import com.logistics.userauth.audit.domain.AuditLog;
import com.logistics.userauth.user.adapters.out.persistence.UserPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogPersistenceMapper {

    private final AuditActionTypeMapper actionTypeMapper;
    private final UserPersistenceMapper userMapper;

    public AuditLogEntity toEntity(AuditLog domain) {
        return AuditLogEntity.builder()
                .id(domain.getId())
                .user(userMapper.toEntity(domain.getUser()))
                .actionType(actionTypeMapper.toEntity(domain.getActionType()))
                .actorIdentifier(domain.getActorIdentifier())
                .ipAddress(domain.getIpAddress())
                .newValues(domain.getNewValues())
                .performedAt(domain.getPerformedAt())
                .tableName(domain.getTableName())
                .recordId(domain.getRecordId())
                .build();
    }

    public AuditLog toDomain(AuditLogEntity entity) {
        return AuditLog.builder()
                .id(entity.getId())
                .user(userMapper.toDomain(entity.getUser()))
                .actionType(actionTypeMapper.toDomain(entity.getActionType()))
                .actorIdentifier(entity.getActorIdentifier())
                .ipAddress(entity.getIpAddress())
                .newValues(entity.getNewValues())
                .performedAt(entity.getPerformedAt())
                .tableName(entity.getTableName())
                .recordId(entity.getRecordId())
                .build();
    }
}
package com.logistics.userauth.audit.adapters.out.persistence;

import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.shared.audit_action.persistence.AuditActionTypeMapper;
import com.logistics.userauth.audit.app.out.AuditLogRepository;
import com.logistics.userauth.audit.domain.AuditLog;
import com.logistics.userauth.user.adapters.out.persistence.UserPersistenceMapper;
import com.logistics.userauth.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditLogPersistenceAdapter implements AuditLogRepository {

    private final AuditLogJpaRepository jpaRepo;
    private final AuditLogPersistenceMapper auditLogMapper;
    private final UserPersistenceMapper userMapper;
    private final AuditActionTypeMapper auditActionTypeMapper;

    @Override
    public void save(AuditLog auditLog) {
        AuditLogEntity auditLogEntity = auditLogMapper.toEntity(auditLog);
        jpaRepo.save(auditLogEntity);
    }

    @Override
    public void delete(AuditLog auditLog) {
        AuditLogEntity auditLogEntity = auditLogMapper.toEntity(auditLog);
        jpaRepo.delete(auditLogEntity);
    }

    @Override
    public Optional<AuditLog> findByUser(User user) {
        return jpaRepo.findByUser(userMapper.toEntity(user)).map(auditLogMapper::toDomain);
    }

    @Override
    public Optional<AuditLog> findByActionType(AuditActionType actionType) {
        return jpaRepo.findByActionType(auditActionTypeMapper.toEntity(actionType)).map(auditLogMapper::toDomain);
    }

    @Override
    public Optional<AuditLog> findByActorIdentifier(String actorIdentifier) {
        return jpaRepo.findByActorIdentifier(actorIdentifier).map(auditLogMapper::toDomain);
    }
}
package com.logistics.userauth.audit.adapters.out.persistence;

import com.logistics.shared.audit_action.persistence.AuditActionTypeEntity;
import com.logistics.userauth.user.adapters.out.persistence.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "audit_logs",
        schema = "user_management",
        indexes = {
                @Index(columnList = "user_id", name = "idx_audit_logs_user_id"),
                @Index(columnList = "action_type_id", name = "idx_audit_logs_action_type_id"),
                @Index(columnList = "table_name, record_id", name = "idx_audit_logs_record"),
                @Index(columnList = "performed_at", name = "idx_audit_logs_performed_at")
        }
)
@Builder
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_type_id")
    private AuditActionTypeEntity actionType;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "actor_identifier")
    private String actorIdentifier;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_values", columnDefinition = "jsonb")
    private Map<String, Object> newValues;

    @CreatedDate
    @Column(name = "performed_at")
    private LocalDateTime performedAt;

    @Column(name = "ip_address", columnDefinition = "inet")
    private String ipAddress;
}
package com.logistics.userauth.audit.domain;

import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.userauth.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditLog {

    private long id;

    private User user;

    private AuditActionType actionType;

    private String tableName;

    private long recordId;

    private String actorIdentifier;

    private Map<String, Object> newValues;

    private LocalDateTime performedAt;

    private String ipAddress;
}
package com.logistics.userauth.user.application.ports.out;

import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;

import java.util.Optional;

public interface UserRepository {
    void save(User user);

    void delete(User user);

    Optional<User> findById(Long id);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmail(String email);

    Optional<User> findByRole(UserRole role);

    Optional<User> findByFacilityId(long id);

}
package com.logistics.userauth.user.adapters.in;

import com.logistics.userauth.user.adapters.in.dto.UserDTO;
import com.logistics.userauth.user.domain.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserControllerMapper {
    public static UserDTO toDTO(User user) {
        return UserDTO.builder()
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .role(user.getRole())
                .build();
    }

    public static User toUser(UserDTO userDTO) {
        return User.builder()
                .phone(userDTO.getPhone())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .middleName(userDTO.getMiddleName())
                .role(userDTO.getRole())
                .lastAccessedTime(LocalDateTime.now())
                .build();
    }
}
package com.logistics.userauth.user.adapters.in.dto;

import com.logistics.userauth.user.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private String phone;

    private String firstName;
    private String lastName;
    private String middleName;

    private UserRole role;

}
package com.logistics.userauth.user.adapters.in.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacilityDTO {
    private Long id;
    private String name;
    private String address;
}
package com.logistics.userauth.user.adapters.out.persistence;

import com.logistics.userauth.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public UserEntity toEntity(User domain) {
        return UserEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .phone(domain.getPhone())
                .passwordHash(domain.getPasswordHash())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .middleName(domain.getMiddleName())
                .role(domain.getRole())
                .facilityId(domain.getFacilityId())
                .status(domain.getStatus())
                .lastAccessedAt(domain.getLastAccessedTime())
                .build();
    }

    public User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .passwordHash(entity.getPasswordHash())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .middleName(entity.getMiddleName())
                .role(entity.getRole())
                .facilityId(entity.getFacilityId())
                .status(entity.getStatus())
                .build();
    }
}
package com.logistics.userauth.user.adapters.out.persistence;

import com.logistics.userauth.user.application.ports.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpaRepo;
    private final UserPersistenceMapper mapper;

    @Override
    public void save(User user) {
        UserEntity entity = mapper.toEntity(user);
        jpaRepo.save(entity);
    }

    @Override
    public void delete(User user) {
        UserEntity entity = mapper.toEntity(user);
        jpaRepo.delete(entity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return jpaRepo.findByPhone(phone).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepo.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByRole(UserRole role) {
        return jpaRepo.findByRole(role).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByFacilityId(long id) {
        return jpaRepo.findByFacilityId(id).map(mapper::toDomain);
    }


}
package com.logistics.userauth.user.adapters.out.persistence;

import com.logistics.userauth.user.domain.UserRole;
import com.logistics.userauth.user.domain.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "users",
        schema = "user_management",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "phone")
        },
        indexes = {
                @Index(columnList = "email", name = "idx_users_email"),
                @Index(columnList = "phone", name = "idx_users_phone"),
                @Index(columnList = "role_name", name = "idx_users_role_name"),
                @Index(columnList = "last_accessed", name = "idx_users_last_accessed"),
                @Index(columnList = "facility_id", name = "idx_users_facility_id")
        }
)
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email")
    @Email
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", nullable = false)
    private UserRole role;

    @Column(name = "facility_id")
    private Long facilityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status")
    private UserStatus status;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

}
package com.logistics.userauth.user.adapters.out.persistence;

import com.logistics.userauth.user.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByPhone(String phone);

    Optional<UserEntity> findByRole(UserRole role);

    Optional<UserEntity> findByFacilityId(long id);

}package com.logistics.userauth.user.domain;

public enum UserRole {
    UNREGISTERED_CONTACT,
    CLIENT,
    PVZ_OPERATOR, PVZ_ADMIN,
    COURIER, DRIVER,
    DISPATCHER,
    WAREHOUSE_OPERATOR, WAREHOUSE_ADMIN,
    HR, ACCOUNTANT, SYSTEM_ADMIN,
    SYSTEM
}
package com.logistics.userauth.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private Long id;

    private String email;

    private String phone;

    private String passwordHash;

    private String firstName;

    private String lastName;

    private String middleName;

    private UserRole role;

    private Long facilityId;

    private LocalDateTime lastAccessedTime;

    private UserStatus status;

}
package com.logistics.userauth.user.domain;

public enum UserStatus {
    ACTIVE,
    ON_DELETE // При установке такого статуса, на следующий день аккаунт удаляется (установка может быть по собственному желанию, может при увольнении)
}package com.logistics.userauth.auth.session.application.ports.out;

import com.logistics.userauth.auth.session.domain.UserSession;
import com.logistics.userauth.user.domain.User;

import java.util.Optional;

public interface UserSessionRepository {
    Optional<UserSession> findByUser(User user);

    Optional<UserSession> findBySessionToken(String sessionToken);

    void save(UserSession userSession);

    void delete(UserSession userSession);
}
package com.logistics.userauth.auth.session.adapters.in;

import com.logistics.userauth.auth.session.adapters.in.dto.UserSessionDTO;
import com.logistics.userauth.auth.session.domain.UserSession;
import org.springframework.stereotype.Component;

@Component
public class UserSessionControllerMapper {

    public UserSession toUserSession(UserSessionDTO userSessionDTO) {
        return UserSession.builder()
                .createdAt(userSessionDTO.getCreatedAt())
                .ipAddress(userSessionDTO.getIpAddress())
                .userAgent(userSessionDTO.getUserAgent())
                .expiresAt(userSessionDTO.getExpiresAt())
                .build();
    }

    public UserSessionDTO toUserSessionDTO(UserSession userSession) {
        return UserSessionDTO.builder()
                .createdAt(userSession.getCreatedAt())
                .ipAddress(userSession.getIpAddress())
                .userAgent(userSession.getUserAgent())
                .expiresAt(userSession.getExpiresAt())
                .build();
    }
}
package com.logistics.userauth.auth.session.adapters.in.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSessionDTO {

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private String ipAddress;

    private String userAgent;
}
package com.logistics.userauth.auth.session.adapters.out.persistence;

import com.logistics.userauth.user.adapters.out.persistence.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "user_sessions",
        schema = "user_management",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "session_token")
        },
        indexes = {
                @Index(columnList = "user_id", name = "idx_user_sessions_user_id"),
                @Index(columnList = "expires_at", name = "idx_user_sessions_expires_at")
        }
)
@Builder
public class UserSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "session_token", nullable = false)
    private String sessionToken;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "ip_address", columnDefinition = "inet")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent; // Браузер

}
package com.logistics.userauth.auth.session.adapters.out.persistence;

import com.logistics.userauth.auth.session.domain.UserSession;
import com.logistics.userauth.user.adapters.out.persistence.UserPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSessionPersistenceMapper {

    private final UserPersistenceMapper upMapper;

    public UserSession toDomain(UserSessionEntity entity) {
        return UserSession.builder()
                .id(entity.getId())
                .user(upMapper.toDomain(entity.getUser()))
                .expiresAt(entity.getExpiresAt())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .createdAt(entity.getCreatedAt())
                .sessionToken(entity.getSessionToken())
                .build();
    }

    public UserSessionEntity toEntity(UserSession domain) {
        return UserSessionEntity.builder()
                .id(domain.getId())
                .user(upMapper.toEntity(domain.getUser()))
                .sessionToken(domain.getSessionToken())
                .createdAt(domain.getCreatedAt())
                .expiresAt(domain.getExpiresAt())
                .ipAddress(domain.getIpAddress())
                .userAgent(domain.getUserAgent())
                .build();
    }
}
package com.logistics.userauth.auth.session.adapters.out.persistence;

import com.logistics.userauth.auth.session.application.ports.out.UserSessionRepository;
import com.logistics.userauth.auth.session.domain.UserSession;
import com.logistics.userauth.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserSessionPersistenceAdapter implements UserSessionRepository {

    private final UserSessionJpaRepository jpaRepo;
    private final UserSessionPersistenceMapper mapper;

    @Override
    public Optional<UserSession> findByUser(User user) {
        return jpaRepo.findByUser(user).map(mapper::toDomain);
    }

    @Override
    public Optional<UserSession> findBySessionToken(String sessionToken) {
        return jpaRepo.findBySessionToken(sessionToken).map(mapper::toDomain);
    }

    @Override
    public void save(UserSession userSession) {
        UserSessionEntity entity = mapper.toEntity(userSession);
        jpaRepo.save(entity);
    }

    @Override
    public void delete(UserSession userSession) {
        UserSessionEntity entity = mapper.toEntity(userSession);
        jpaRepo.delete(entity);
    }
}
package com.logistics.userauth.auth.session.adapters.out.persistence;

import com.logistics.userauth.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionJpaRepository extends JpaRepository<UserSessionEntity, Long> {
    Optional<UserSessionEntity> findByUser(User user);

    Optional<UserSessionEntity> findBySessionToken(String sessionToken);

}
package com.logistics.userauth.auth.session.domain;

import com.logistics.userauth.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSession {

    private long id;

    private User user;

    private String sessionToken;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private String ipAddress;

    private String userAgent;

}
package com.logistics.userauth;

import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public @interface IntegrationTest {
}
