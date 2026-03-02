package com.auto.car.api.dto;

import com.auto.car.api.enums.ContactType;
import com.auto.car.api.enums.OwnerType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactDto {
    @JsonIgnore
    private String id;
    private OwnerType ownerType;

    @JsonIgnore
    private String userId;

    @JsonIgnore
    private String companyId;

    private ContactType contactType;
    private String value;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
}

