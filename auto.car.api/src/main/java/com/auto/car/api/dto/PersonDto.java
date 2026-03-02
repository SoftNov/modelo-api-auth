package com.auto.car.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDto {
    @JsonIgnore
    private String id;

    @JsonIgnore
    private String userId;

    private String fullName;
    private String cpf;
    private LocalDate birthDate;
    private LocalDateTime createdAt;
}

