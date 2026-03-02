package com.auto.car.api.dto;

import com.auto.car.api.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    @JsonIgnore
    private String id;

    private String username;

    private String password;

    private String confirmPassword;

    @JsonIgnore
    private LocalDateTime emailVerifiedAt;

    @JsonIgnore
    private LocalDateTime lastLoginAt;

    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonIgnore
    private LocalDateTime updatedAt;

    private PersonDto person;
    private CompanyDto company;
    private List<ContactDto> contacts;
    private AddressDto address;

    @JsonIgnore
    private List<RoleDto> roles;
}
