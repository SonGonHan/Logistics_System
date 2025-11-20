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
