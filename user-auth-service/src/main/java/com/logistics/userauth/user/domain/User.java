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
