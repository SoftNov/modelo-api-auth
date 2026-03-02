package com.auto.car.api.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilitário para criptografar e descriptografar dados do usuário dentro do JWT.
 * Utiliza AES-GCM para garantir confidencialidade e integridade dos dados.
 */
@Component
@Log4j2
public class JwtDataEncryptor {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    @Value("${jwt.data.encryption.key}")
    private String encryptionKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Criptografa os dados do usuário para inclusão no JWT.
     *
     * @param userData dados do usuário
     * @return string criptografada em Base64
     */
    public String encrypt(JwtUserData userData) {
        try {
            String json = objectMapper.writeValueAsString(userData);
            return encryptString(json);
        } catch (JsonProcessingException e) {
            log.error("Erro ao serializar dados do usuário: {}", e.getMessage());
            throw new RuntimeException("Erro ao criptografar dados do usuário", e);
        }
    }

    /**
     * Descriptografa os dados do usuário do JWT.
     *
     * @param encryptedData string criptografada em Base64
     * @return dados do usuário
     */
    public JwtUserData decrypt(String encryptedData) {
        try {
            String json = decryptString(encryptedData);
            return objectMapper.readValue(json, JwtUserData.class);
        } catch (JsonProcessingException e) {
            log.error("Erro ao deserializar dados do usuário: {}", e.getMessage());
            throw new RuntimeException("Erro ao descriptografar dados do usuário", e);
        }
    }

    private String encryptString(String plainText) {
        try {
            byte[] keyBytes = getKeyBytes();
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

            // Gera IV aleatório
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Concatena IV + dados criptografados
            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            log.error("Erro ao criptografar string: {}", e.getMessage());
            throw new RuntimeException("Erro na criptografia", e);
        }
    }

    private String decryptString(String encryptedText) {
        try {
            byte[] keyBytes = getKeyBytes();
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

            byte[] combined = Base64.getDecoder().decode(encryptedText);

            // Extrai IV e dados criptografados
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedBytes = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, encryptedBytes, 0, encryptedBytes.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Erro ao descriptografar string: {}", e.getMessage());
            throw new RuntimeException("Erro na descriptografia", e);
        }
    }

    private byte[] getKeyBytes() {
        // Garante que a chave tenha exatamente 32 bytes (256 bits) para AES-256
        byte[] keyBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);
        byte[] key = new byte[32];
        System.arraycopy(keyBytes, 0, key, 0, Math.min(keyBytes.length, 32));
        return key;
    }
}

