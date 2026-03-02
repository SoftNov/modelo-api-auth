package com.auto.car.api.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenericMapper {
    private final ObjectMapper objectMapper;

    @Autowired
    public GenericMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <S, T> T map(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        return objectMapper.convertValue(source, targetClass);
    }
}
