package com.auto.car.api.enums.error;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorEnum {
    // Erros genéricos (001.xxx)
    ERROR_GENERIC("001.001", "Ocorreu um erro inesperado!", "Não foi possível realizar rquisição. Tente novamente mais tarde!", HttpStatus.INTERNAL_SERVER_ERROR),

    // Erros de validação de cadastro (001.xxx)
    ERROR_CPF("001.002", "CPF inválido!", "O CPF informado não é válido. Verifique os dígitos e tente novamente.", HttpStatus.BAD_REQUEST),
    ERROR_CNPJ("001.003", "CNPJ inválido!", "O CNPJ informado não é válido. Verifique os dígitos e tente novamente.", HttpStatus.BAD_REQUEST),
    ERROR_BIRTH_DATE("001.004", "Data de nascimento inválida!", "A data de nascimento informada é inválida. Verifique o formato (dd/MM/yyyy) e se a data não é futura.", HttpStatus.BAD_REQUEST),
    ERROR_EMAIL("001.005", "E-mail inválido!", "O endereço de e-mail informado não é válido. Verifique o formato e tente novamente.", HttpStatus.BAD_REQUEST),
    ERROR_PASSWORD("001.006", "Senha inválida!", "A senha deve ter no mínimo 8 caracteres, contendo letras, números e caracteres especiais.", HttpStatus.BAD_REQUEST),
    ERROR_PASSWORD_MISMATCH("001.007", "Senhas não coincidem!", "A senha e a confirmação de senha devem ser iguais.", HttpStatus.BAD_REQUEST),
    ERROR_SEND_EMAIL("001.008", "Erro ao enviar e-mail!", "Não foi possível enviar o e-mail de confirmação. Tente novamente mais tarde.", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_USER_ALREADY_ACTIVE("001.009", "Usuário já cadastrado!", "Já existe uma conta ativa com este nome de usuário. Faça login ou recupere sua senha.", HttpStatus.CONFLICT),
    ERROR_USER_PENDING_RESEND("001.010", "Link de ativação reenviado!", "Sua conta está pendente de verificação. Um novo link de ativação foi enviado para seu e-mail.", HttpStatus.OK),

    // Erros de confirmação de e-mail (002.xxx)
    ERROR_USER_NOT_FOUND("002.001", "Usuário não encontrado!", "O usuário informado não foi encontrado.", HttpStatus.NOT_FOUND),
    ERROR_TOKEN_INVALID("002.002", "Token inválido!", "O token de confirmação informado não é válido.", HttpStatus.BAD_REQUEST),
    ERROR_TOKEN_ALREADY_USED("002.003", "Token já utilizado!", "Este link de confirmação já foi utilizado anteriormente.", HttpStatus.BAD_REQUEST),
    ERROR_TOKEN_EXPIRED("002.004", "Token expirado!", "O link de confirmação expirou. Solicite um novo e-mail de confirmação.", HttpStatus.BAD_REQUEST),
    ERROR_EMAIL_ALREADY_CONFIRMED("002.005", "E-mail já confirmado!", "O e-mail deste usuário já foi confirmado anteriormente.", HttpStatus.BAD_REQUEST),

    // Erros de autenticação (003.xxx)
    ERROR_INVALID_CREDENTIALS("003.001", "Credenciais inválidas!", "Usuário ou senha incorretos.", HttpStatus.UNAUTHORIZED),
    ERROR_EMAIL_NOT_VERIFIED("003.002", "E-mail não verificado!", "Confirme seu e-mail antes de fazer login. Verifique sua caixa de entrada.", HttpStatus.FORBIDDEN),
    ERROR_USER_BLOCKED("003.003", "Usuário bloqueado!", "Sua conta está bloqueada. Entre em contato com o suporte.", HttpStatus.FORBIDDEN),
    ERROR_INVALID_REFRESH_TOKEN("003.004", "Token de atualização inválido!", "O token de atualização é inválido ou expirou. Faça login novamente.", HttpStatus.UNAUTHORIZED),
    ERROR_CLIENT_ID_MISSING("003.005", "Client-Id ausente!", "O header X-Client-Id é obrigatório para esta requisição.", HttpStatus.BAD_REQUEST),
    ERROR_CLIENT_ID_MISMATCH("003.006", "Client-Id inválido!", "O Client-Id informado não corresponde ao usuário autenticado.", HttpStatus.FORBIDDEN),
    ERROR_INVALID_TOKEN_DATA("003.007", "Dados do token inválidos!", "Não foi possível validar os dados do token. Faça login novamente.", HttpStatus.UNAUTHORIZED),

    // Erros de reset de senha (004.xxx)
    ERROR_RESET_USER_NOT_FOUND("004.001", "Usuário não encontrado!", "Não existe conta associada a este e-mail/usuário.", HttpStatus.NOT_FOUND),
    ERROR_RESET_CODE_INVALID("004.002", "Código inválido!", "O código informado é inválido ou já foi utilizado.", HttpStatus.BAD_REQUEST),
    ERROR_RESET_CODE_EXPIRED("004.003", "Código expirado!", "O código de verificação expirou. Solicite um novo código.", HttpStatus.BAD_REQUEST),
    ERROR_RESET_CODE_NOT_VALIDATED("004.004", "Código não validado!", "Você precisa validar o código antes de redefinir a senha.", HttpStatus.BAD_REQUEST),
    ERROR_RESET_TOO_MANY_REQUESTS("004.005", "Muitas solicitações!", "Você atingiu o limite de solicitações. Aguarde 30 minutos para tentar novamente.", HttpStatus.TOO_MANY_REQUESTS),
    ERROR_RESET_SEND_EMAIL("004.006", "Erro ao enviar código!", "Não foi possível enviar o código por e-mail. Tente novamente.", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_RESET_PASSWORD_ALREADY_USED("004.007", "Senha já utilizada!", "Esta senha já foi utilizada anteriormente. Escolha uma senha diferente das últimas 5 utilizadas.", HttpStatus.BAD_REQUEST),
    ERROR_RESET_BLOCKED_TEMPORARILY("004.008", "Bloqueio temporário!", "Você solicitou muitos códigos recentemente. Aguarde 30 minutos para tentar novamente.", HttpStatus.TOO_MANY_REQUESTS),

    ;
    private final String code;
    private final String title;
    private final String message;
    private final HttpStatus httpStatus;
}
