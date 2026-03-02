package com.auto.car.api.exception.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class ErrorGeneric {

    public ErrorGeneric(int httpStatusCode, String errorCode, String errorMessage, String TitleErro) {
        this.httpStatusCode = httpStatusCode;
        this.code = errorCode;
        this.message = errorMessage;
        this.title = TitleErro;
    }

    private int httpStatusCode;
    private String code;
    private String title;
    private String message;
}
