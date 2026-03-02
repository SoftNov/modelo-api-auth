package com.auto.car.api.service.impl;

import com.auto.car.api.config.ApplicationConfig;
import com.auto.car.api.enums.error.EmailErrorEnum;
import com.auto.car.api.exception.ClientRequestException;
import com.auto.car.api.service.dto.SendMailRequest;
import com.auto.car.api.service.SendMailService;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class SendMailServiceImpl implements SendMailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private ApplicationConfig applicationConfig;

    @Override
    public void sendMail(SendMailRequest sendMail) {
        // Validações
        validateRequest(sendMail);

        try {
            log.info("Enviando e-mail para: {}", sendMail.getTo());

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(applicationConfig.getEmailFrom());
            helper.setTo(sendMail.getTo());
            helper.setSubject(sendMail.getSubject());
            helper.setText(sendMail.getContent(), true);

            // Adicionando anexo se existir
            if (sendMail.getDocument() != null && sendMail.getDocument().length > 0) {
                String fileName = sendMail.getDocumentName() != null ? sendMail.getDocumentName() : "Anexo";
                ByteArrayDataSource dataSource = new ByteArrayDataSource(sendMail.getDocument(), "application/pdf");
                helper.addAttachment(fileName + ".pdf", dataSource);
                log.info("Anexo adicionado: {}.pdf", fileName);
            }

            javaMailSender.send(message);
            log.info("E-mail enviado com sucesso para: {}", sendMail.getTo());

        } catch (Exception e) {
            log.error("Erro ao enviar e-mail para {}: {}", sendMail.getTo(), e.getMessage());
            throwError(EmailErrorEnum.SEND_EMAIL_ERROR);
        }
    }

    /**
     * Valida os dados da requisição de envio de e-mail.
     */
    private void validateRequest(SendMailRequest sendMail) {
        if (sendMail.getTo() == null || sendMail.getTo().trim().isEmpty()) {
            throwError(EmailErrorEnum.EMAIL_RECIPIENT_REQUIRED);
        }

        if (sendMail.getSubject() == null || sendMail.getSubject().trim().isEmpty()) {
            throwError(EmailErrorEnum.EMAIL_SUBJECT_REQUIRED);
        }

        if (sendMail.getContent() == null || sendMail.getContent().trim().isEmpty()) {
            throwError(EmailErrorEnum.EMAIL_CONTENT_REQUIRED);
        }
    }

    /**
     * Lança exceção de erro a partir do enum.
     */
    private void throwError(EmailErrorEnum error) {
        throw new ClientRequestException(
                error.getHttpStatus(),
                error.getCode(),
                error.getTitle(),
                error.getMessage()
        );
    }
}
