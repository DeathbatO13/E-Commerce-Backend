package com.e_commerce.catalog_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${security.jwt.secret}")
    private String secret;

    public Claims getClaims(String token) {

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            // Si el token expiró, la firma es incorrecta o está mal formado, lanzará una excepción.
            this.getClaims(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.debug("El token JWT ha expirado: {}", e.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.warn("Firma del JWT inválida: {}", e.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.warn("Token JWT mal formado: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("Error al validar el token JWT: {}", e.getMessage());
        }
        return false;
    }
}
