package com.auto.car.api.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DocumentValidator {

    private DocumentValidator() {
        // Classe utilitária - não deve ser instanciada
    }

    /**
     * Valida um CPF.
     * Aceita CPF com ou sem formatação (ex: "123.456.789-09" ou "12345678909").
     *
     * @param cpf o CPF a ser validado
     * @return true se o CPF for válido, false caso contrário
     */
    public static boolean isValidCPF(String cpf) {
        if (cpf == null) {
            return false;
        }

        // Remove caracteres não numéricos
        String digits = cpf.replaceAll("[^0-9]", "");

        // CPF deve ter 11 dígitos
        if (digits.length() != 11) {
            return false;
        }

        // Verifica se todos os dígitos são iguais (CPF inválido)
        if (allDigitsSame(digits)) {
            return false;
        }

        // Converte para array de inteiros
        int[] nums = digits.chars().map(c -> c - '0').toArray();

        // Calcula o primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += nums[i] * (10 - i);
        }
        int remainder = sum % 11;
        int firstDigit = (remainder < 2) ? 0 : 11 - remainder;

        if (nums[9] != firstDigit) {
            return false;
        }

        // Calcula o segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += nums[i] * (11 - i);
        }
        remainder = sum % 11;
        int secondDigit = (remainder < 2) ? 0 : 11 - remainder;

        return nums[10] == secondDigit;
    }

    /**
     * Valida um CNPJ.
     * Aceita CNPJ com ou sem formatação (ex: "12.345.678/0001-95" ou "12345678000195").
     * Também aceita o novo formato alfanumérico onde os 8 primeiros caracteres podem ser letras.
     * (ex: "12.ABC.345/0001-XX" ou "12ABC345000195")
     *
     * @param cnpj o CNPJ a ser validado
     * @return true se o CNPJ for válido, false caso contrário
     */
    public static boolean isValidCNPJ(String cnpj) {
        if (cnpj == null) {
            return false;
        }

        // Remove caracteres de formatação (pontos, traços, barras)
        String cleaned = cnpj.replaceAll("[.\\-/]", "").toUpperCase();

        // CNPJ deve ter 14 caracteres
        if (cleaned.length() != 14) {
            return false;
        }

        // Verifica se contém apenas caracteres válidos (letras A-Z e números 0-9)
        if (!cleaned.matches("[A-Z0-9]{14}")) {
            return false;
        }

        // Verifica se todos os caracteres são iguais (CNPJ inválido)
        if (allCharsSame(cleaned)) {
            return false;
        }

        // Converte para array de valores numéricos
        // Números: valor = caractere - '0' (0-9)
        // Letras: valor = caractere - 'A' + 10 (10-35, similar a base36)
        int[] nums = new int[14];
        for (int i = 0; i < 14; i++) {
            char c = cleaned.charAt(i);
            if (Character.isDigit(c)) {
                nums[i] = c - '0';
            } else {
                nums[i] = c - 'A' + 10;
            }
        }

        // Pesos para cálculo do primeiro dígito verificador
        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        // Calcula o primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += nums[i] * weights1[i];
        }
        int remainder = sum % 11;
        int firstDigit = (remainder < 2) ? 0 : 11 - remainder;

        if (nums[12] != firstDigit) {
            return false;
        }

        // Pesos para cálculo do segundo dígito verificador
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        // Calcula o segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 13; i++) {
            sum += nums[i] * weights2[i];
        }
        remainder = sum % 11;
        int secondDigit = (remainder < 2) ? 0 : 11 - remainder;

        return nums[13] == secondDigit;
    }

    /**
     * Valida uma data de nascimento.
     * Verifica se a data é válida, se não é futura e se a idade está entre limites razoáveis.
     *
     * @param birthDate a data de nascimento a ser validada
     * @return true se a data de nascimento for válida, false caso contrário
     */
    public static boolean isValidBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            return false;
        }

        LocalDate now = LocalDate.now();

        // Data não pode ser futura
        if (birthDate.isAfter(now)) {
            return false;
        }

        // Calcula a idade
        int age = Period.between(birthDate, now).getYears();

        // Idade deve estar entre 0 e 150 anos (limites razoáveis)
        if (age < 0 || age > 150) {
            return false;
        }

        // Para cadastro de usuário, pode ser útil ter idade mínima (ex: 16 anos)
        // Descomente a linha abaixo se necessário
        // return age >= 16;

        return true;
    }

    /**
     * Valida uma data de nascimento em formato String.
     * Aceita formatos: "dd/MM/yyyy", "yyyy-MM-dd".
     *
     * @param birthDateStr a data de nascimento em formato String
     * @return true se a data de nascimento for válida, false caso contrário
     */
    public static boolean isValidBirthDate(String birthDateStr) {
        if (birthDateStr == null || birthDateStr.trim().isEmpty()) {
            return false;
        }

        try {
            LocalDate birthDate = parseBirthDate(birthDateStr);
            return isValidBirthDate(birthDate);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Converte string de data para LocalDate.
     * Suporta formatos: "dd/MM/yyyy", "yyyy-MM-dd".
     *
     * @param dateStr a data em formato String
     * @return LocalDate correspondente
     * @throws DateTimeParseException se o formato for inválido
     */
    private static LocalDate parseBirthDate(String dateStr) {
        dateStr = dateStr.trim();

        // Formato brasileiro: dd/MM/yyyy
        if (dateStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(dateStr, formatter);
        }

        // Formato ISO: yyyy-MM-dd
        if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return LocalDate.parse(dateStr);
        }

        throw new DateTimeParseException("Formato de data inválido", dateStr, 0);
    }

    /**
     * Valida um endereço de e-mail.
     * Verifica se o formato está correto usando regex baseado no padrão RFC 5322.
     *
     * @param email o endereço de e-mail a ser validado
     * @return true se o e-mail for válido, false caso contrário
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        email = email.trim();

        // Verifica tamanho máximo (254 caracteres é o limite RFC)
        if (email.length() > 254) {
            return false;
        }

        // Regex baseado no padrão RFC 5322 (simplificado)
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                           "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        if (!email.matches(emailRegex)) {
            return false;
        }

        // Verifica se não há pontos consecutivos
        if (email.contains("..")) {
            return false;
        }

        // Verifica se não começa ou termina com ponto
        String localPart = email.split("@")[0];
        if (localPart.startsWith(".") || localPart.endsWith(".")) {
            return false;
        }

        // Verifica tamanho da parte local (antes do @) - máximo 64 caracteres
        if (localPart.length() > 64) {
            return false;
        }

        return true;
    }

    /**
     * Extrai o e-mail da lista de contatos do usuário.
     * Busca o primeiro contato com contactType = EMAIL.
     *
     * @param contacts lista de contatos do usuário
     * @return o e-mail encontrado ou null se não encontrar
     */
    private static String extractEmailFromContacts(java.util.List<com.auto.car.api.dto.ContactDto> contacts) {
        if (contacts == null || contacts.isEmpty()) {
            return null;
        }

        return contacts.stream()
                .filter(contact -> contact.getContactType() == com.auto.car.api.enums.ContactType.EMAIL)
                .map(contact -> contact.getValue())
                .findFirst()
                .orElse(null);
    }

    /**
     * Valida o e-mail extraído da lista de contatos do usuário.
     *
     * @param contacts lista de contatos do usuário
     * @return true se o e-mail for válido, false caso contrário
     */
    public static boolean isValidEmailFromContacts(java.util.List<com.auto.car.api.dto.ContactDto> contacts) {
        String email = extractEmailFromContacts(contacts);
        return isValidEmail(email);
    }

    /**
     * Verifica se todos os dígitos da string são iguais.
     *
     * @param str a string a ser verificada
     * @return true se todos os dígitos forem iguais, false caso contrário
     */
    private static boolean allDigitsSame(String str) {
        char first = str.charAt(0);
        for (int i = 1; i < str.length(); i++) {
            if (str.charAt(i) != first) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica se todos os caracteres da string são iguais.
     *
     * @param str a string a ser verificada
     * @return true se todos os caracteres forem iguais, false caso contrário
     */
    private static boolean allCharsSame(String str) {
        char first = str.charAt(0);
        for (int i = 1; i < str.length(); i++) {
            if (str.charAt(i) != first) {
                return false;
            }
        }
        return true;
    }
}
