package com.auto.car.api.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Classe utilitária para operações relacionadas a senhas.
 * Centraliza validação e criptografia de senhas usando BCrypt.
 */
public final class PasswordUtils {

    /**
     * BCrypt com strength 12 - padrão seguro para senhas.
     * Strength 12 significa 2^12 iterações, oferecendo bom equilíbrio entre segurança e performance.
     */
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder(12);

    /**
     * Tamanho mínimo da senha
     */
    private static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Regex para validar presença de letra
     */
    private static final String LETTER_PATTERN = ".*[a-zA-Z].*";

    /**
     * Regex para validar presença de dígito
     */
    private static final String DIGIT_PATTERN = ".*\\d.*";

    /**
     * Regex para validar presença de caractere especial
     */
    private static final String SPECIAL_CHAR_PATTERN = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*";

    private PasswordUtils() {
        // Classe utilitária - não deve ser instanciada
    }

    /**
     * Valida a senha conforme as regras de segurança.
     * Regras:
     * - Mínimo de 8 caracteres
     * - Deve conter pelo menos uma letra (a-z, A-Z)
     * - Deve conter pelo menos um número (0-9)
     * - Deve conter pelo menos um caractere especial (!@#$%^&*()_+-=[]{}etc.)
     *
     * @param password a senha a ser validada
     * @return true se a senha for válida, false caso contrário
     */
    public static boolean isValid(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }

        boolean hasLetter = password.matches(LETTER_PATTERN);
        boolean hasDigit = password.matches(DIGIT_PATTERN);
        boolean hasSpecialChar = password.matches(SPECIAL_CHAR_PATTERN);

        return hasLetter && hasDigit && hasSpecialChar;
    }

    /**
     * Valida se a senha e a confirmação de senha são iguais.
     *
     * @param password a senha
     * @param confirmPassword a confirmação da senha
     * @return true se as senhas forem iguais, false caso contrário
     */
    public static boolean matches(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }

    /**
     * Criptografa a senha usando BCrypt.
     * BCrypt é um algoritmo de hash de senha que inclui salt automático,
     * tornando-o resistente a ataques de rainbow table.
     *
     * @param password a senha a ser criptografada
     * @return o hash BCrypt da senha
     */
    public static String encrypt(String password) {
        return PASSWORD_ENCODER.encode(password);
    }

    /**
     * Verifica se a senha informada corresponde ao hash armazenado.
     * Útil para validação durante o login.
     *
     * @param rawPassword a senha em texto puro
     * @param encodedPassword o hash BCrypt armazenado
     * @return true se a senha corresponder ao hash, false caso contrário
     */
    public static boolean verify(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }

    /**
     * Valida a senha e confirmação, retornando o hash criptografado se válido.
     * Método de conveniência que combina validação e criptografia.
     *
     * @param password a senha
     * @param confirmPassword a confirmação da senha
     * @return o hash BCrypt da senha se válida
     * @throws IllegalArgumentException se a senha for inválida ou não coincidir
     */
    public static String validateAndEncrypt(String password, String confirmPassword) {
        if (!isValid(password)) {
            throw new IllegalArgumentException("Senha inválida");
        }

        if (!matches(password, confirmPassword)) {
            throw new IllegalArgumentException("Senhas não coincidem");
        }

        return encrypt(password);
    }

}

