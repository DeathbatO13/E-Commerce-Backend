package com.e_commerce.catalog_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtProvider {

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
            System.err.println("El token JWT ha expirado: " + e.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException e) {
            System.err.println("Firma del JWT inválida: " + e.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            System.err.println("Token JWT mal formado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al validar el token JWT: " + e.getMessage());
        }
        return false;
    }
}
