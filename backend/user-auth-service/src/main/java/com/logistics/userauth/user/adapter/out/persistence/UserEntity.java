package com.logistics.userauth.user.adapter.out.persistence;

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
                @Index(columnList = "last_accessed_at", name = "idx_users_last_accessed"),
                @Index(columnList = "facility_id", name = "idx_users_facility_id")
        }
)
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(
            name = "users_seq",
            sequenceName = "users_user_id_seq",
            schema = "user_management",
            allocationSize = 1
    )
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
    private LocalDateTime createdAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

}
