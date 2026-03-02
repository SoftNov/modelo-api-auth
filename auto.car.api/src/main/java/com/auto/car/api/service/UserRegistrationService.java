package com.auto.car.api.service;

import com.auto.car.api.dto.UserDto;
import com.auto.car.api.dto.response.UserResponse;

public interface UserRegistrationService {
    UserResponse register(UserDto userCoreDto);
}
