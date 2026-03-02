package com.auto.car.api.dto;

import com.auto.car.api.enums.OwnerType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    @JsonIgnore
    private String id;

    private OwnerType ownerType;

    @JsonIgnore
    private String userId;

    @JsonIgnore
    private String companyId;

    private String zipCode;
    private String street;
    private String number;
    private String complement;
    private String district;
    private String city;
    private String state;
    private String country;

    @JsonIgnore
    private LocalDateTime createdAt;
}

