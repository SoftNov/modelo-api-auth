package com.auto.car.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyUserDto {
    private String companyId;
    private String userId;
    private String role;
    private LocalDateTime createdAt;
}

