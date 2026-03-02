package com.auto.car.api.enums.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enum que centraliza as mensagens de erro relacionadas ao envio de e-mail.
 */
@Getter
@AllArgsConstructor
public enum EmailErrorEnum {

    SEND_EMAIL_ERROR("003.001", "Erro ao enviar e-mail!", "Não foi possível enviar o e-mail. Tente novamente mais tarde.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_EMAIL_ADDRESS("003.002", "E-mail inválido!", "O endereço de e-mail informado não é válido.", HttpStatus.BAD_REQUEST),
    EMAIL_RECIPIENT_REQUIRED("003.003", "Destinatário obrigatório!", "O destinatário do e-mail é obrigatório.", HttpStatus.BAD_REQUEST),
    EMAIL_SUBJECT_REQUIRED("003.004", "Assunto obrigatório!", "O assunto do e-mail é obrigatório.", HttpStatus.BAD_REQUEST),
    EMAIL_CONTENT_REQUIRED("003.005", "Conteúdo obrigatório!", "O conteúdo do e-mail é obrigatório.", HttpStatus.BAD_REQUEST),
    EMAIL_ATTACHMENT_ERROR("003.006", "Erro no anexo!", "Não foi possível anexar o arquivo ao e-mail.", HttpStatus.BAD_REQUEST),

    ;

    private final String code;
    private final String title;
    private final String message;
    private final HttpStatus httpStatus;
}

