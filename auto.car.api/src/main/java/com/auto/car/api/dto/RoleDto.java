package com.auto.car.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDto {
    @JsonIgnore
    private String id;
    private String name;
    private String description;
}

