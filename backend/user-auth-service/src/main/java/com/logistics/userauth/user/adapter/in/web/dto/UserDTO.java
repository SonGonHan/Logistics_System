package com.logistics.userauth.user.adapter.in.web.dto;

import com.logistics.userauth.user.domain.UserRole;
import lombok.Builder;

@Builder
public record UserDTO (String phone,
                       String firstName,
                       String lastName,
                       String middleName,
                       UserRole role){

}
