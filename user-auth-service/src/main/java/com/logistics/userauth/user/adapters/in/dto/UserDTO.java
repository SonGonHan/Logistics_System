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
