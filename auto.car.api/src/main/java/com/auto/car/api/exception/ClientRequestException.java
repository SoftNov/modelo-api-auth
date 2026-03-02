package com.auto.car.api.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Getter
public class ClientRequestException extends ResponseStatusException {
    private static final long serialVersionUID = 1L;

    private final HttpStatus httpStatus;
    private final String code;
    private final String title;
    private final String message;

    public ClientRequestException(Exception e, HttpStatus httpStatus, String code, String title, String message) {
        super(httpStatus, message);
        log.error(e.getMessage(), e);
        this.httpStatus = httpStatus;
        this.code = code;
        this.title = title;
        this.message = message;
    }

    public ClientRequestException( HttpStatus httpStatus, String code, String title, String message) {
        super(httpStatus, message);
        this.httpStatus = httpStatus;
        this.code = code;
        this.title = title;
        this.message = message;
    }
}
