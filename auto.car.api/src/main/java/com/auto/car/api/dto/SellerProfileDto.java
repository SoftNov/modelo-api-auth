package com.auto.car.api.dto;

import com.auto.car.api.repository.entity.CompanyEntity;
import com.auto.car.api.repository.entity.UserEntity;
import com.auto.car.api.enums.SellerType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SellerProfileDto {
    private String id;
    private SellerType sellerType;
    private UserEntity user;
    private CompanyEntity company;
    private BigDecimal rating;
    private Integer totalAds;
    private LocalDateTime createdAt;
}
