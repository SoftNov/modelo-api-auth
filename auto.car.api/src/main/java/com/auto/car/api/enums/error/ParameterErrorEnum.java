package com.auto.car.api.enums.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enum que centraliza as mensagens de erro relacionadas a parâmetros do sistema.
 */
@Getter
@AllArgsConstructor
public enum ParameterErrorEnum {

    PARAMETER_ALREADY_EXISTS("002.001", "Parâmetro já existe!", "Já existe um parâmetro cadastrado com a chave informada.", HttpStatus.CONFLICT),
    PARAMETER_NOT_FOUND("002.002", "Parâmetro não encontrado!", "O parâmetro informado não foi encontrado no sistema.", HttpStatus.NOT_FOUND),
    PARAMETER_NOT_EDITABLE("002.003", "Parâmetro não editável!", "Este parâmetro não pode ser alterado pelo sistema.", HttpStatus.FORBIDDEN),
    PARAMETER_KEY_REQUIRED("002.004", "Chave obrigatória!", "A chave do parâmetro é obrigatória.", HttpStatus.BAD_REQUEST),
    PARAMETER_VALUE_REQUIRED("002.005", "Valor obrigatório!", "O valor do parâmetro é obrigatório.", HttpStatus.BAD_REQUEST),
    PARAMETER_TYPE_REQUIRED("002.006", "Tipo obrigatório!", "O tipo do parâmetro é obrigatório.", HttpStatus.BAD_REQUEST),
    PARAMETER_CATEGORY_REQUIRED("002.007", "Categoria obrigatória!", "A categoria do parâmetro é obrigatória.", HttpStatus.BAD_REQUEST),

    ;

    private final String code;
    private final String title;
    private final String message;
    private final HttpStatus httpStatus;
}

