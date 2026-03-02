package com.auto.car.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Log4j2
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Autowired
    private JwtDataEncryptor jwtDataEncryptor;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Gera o token de acesso JWT com dados do usuário criptografados.
     */
    public String generateAccessToken(Authentication authentication, JwtUserData userData) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateAccessToken(userDetails, userData);
    }

    /**
     * Gera o token de acesso JWT a partir do UserDetails com dados criptografados.
     */
    public String generateAccessToken(UserDetails userDetails, JwtUserData userData) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Criptografa os dados sensíveis do usuário
        String encryptedData = jwtDataEncryptor.encrypt(userData);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", roles)
                .claim("data", encryptedData)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Gera o refresh token JWT.
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrai o username do token JWT.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    /**
     * Extrai os dados criptografados do usuário do token JWT.
     */
    public JwtUserData getUserDataFromToken(String token) {
        Claims claims = getClaims(token);
        String encryptedData = claims.get("data", String.class);

        if (encryptedData == null) {
            log.warn("Token não contém dados do usuário criptografados");
            return null;
        }

        return jwtDataEncryptor.decrypt(encryptedData);
    }

    /**
     * Extrai o ID do usuário do token JWT (dados criptografados).
     */
    public String getUserIdFromToken(String token) {
        JwtUserData userData = getUserDataFromToken(token);
        return userData != null ? userData.getUserId() : null;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Valida o token JWT.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Token JWT inválido: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Token JWT expirado: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Token JWT não suportado: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string está vazia: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Verifica se o token é um refresh token.
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getClaims(token);
            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retorna o tempo de expiração do access token em segundos.
     */
    public long getAccessTokenExpirationInSeconds() {
        return jwtExpiration / 1000;
    }

    /**
     * Retorna o tempo de expiração do refresh token em segundos.
     */
    public long getRefreshTokenExpirationInSeconds() {
        return refreshExpiration / 1000;
    }
}
