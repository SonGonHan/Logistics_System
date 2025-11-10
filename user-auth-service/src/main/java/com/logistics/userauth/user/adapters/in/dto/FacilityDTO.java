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
