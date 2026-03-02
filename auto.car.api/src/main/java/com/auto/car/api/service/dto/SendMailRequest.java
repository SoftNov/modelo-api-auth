package com.auto.car.api.service.dto;

import lombok.*;

/**
 * DTO para requisição de envio de e-mail.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMailRequest {

    /**
     * Endereço de e-mail do destinatário
     */
    private String to;

    /**
     * Assunto do e-mail
     */
    private String subject;

    /**
     * Conteúdo do e-mail (pode ser HTML)
     */
    private String content;

    /**
     * Documento anexo em bytes (opcional)
     */
    private byte[] document;

    /**
     * Nome do arquivo anexo (opcional)
     */
    private String documentName;
}

