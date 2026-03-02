package com.auto.car.api.dto;

import com.auto.car.api.enums.CompanyStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CompanyDto {
    @JsonIgnore
    private String id;

    private String legalName;

    private String tradeName;

    private String cnpj;

    private CompanyStatus status;

    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonIgnore
    private LocalDateTime updatedAt;
}
