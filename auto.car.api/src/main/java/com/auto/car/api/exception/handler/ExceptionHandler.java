package com.auto.car.api.exception.handler;

import com.auto.car.api.exception.ClientRequestException;
import com.auto.car.api.exception.model.ErrorGeneric;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@Log4j2
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler({ ClientRequestException.class })
    public ResponseEntity<Object> handleApiCustomException(ClientRequestException ex) {
        ErrorGeneric erroApi = new ErrorGeneric(
                ex.getHttpStatus().value(),
                ex.getCode(),
                ex.getMessage(),
                ex.getTitle()
        );

        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<>(erroApi, httpHeaders, ex.getHttpStatus());
    }

}
