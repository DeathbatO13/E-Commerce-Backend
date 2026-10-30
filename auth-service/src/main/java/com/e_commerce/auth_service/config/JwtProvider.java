package com.e_commerce.auth_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;


/**
 * Componente responsable de la generación y validación/extracción de claims de tokens JWT.
 * <p>
 * Utiliza la librería JJWT para:
 * <ul>
 *   <li>Crear tokens firmados con HMAC-SHA256 durante el login/registro</li>
 *   <li>Extraer y validar los claims (subject, email, roles, fechas) en el filtro de autenticación</li>
 * </ul>
 * </p>
 * <p>
 * La clave secreta y el tiempo de expiración se leen desde propiedades de configuración
 * (application.yml / application.properties) para facilitar cambios sin recompilar.
 * </p>
 */
@Component
public class JwtProvider{

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${security.jwt.secret}")
    private String secret;

    /**
     * Extrae los claims (payload) de un token JWT.
     * <p>
     * Valida la firma del token usando la clave secreta configurada.
     * Si el token está mal formado, expirado o la firma no coincide,
     * lanza una excepción de tipo JwtException (manejar en el filtro o globalmente).
     * </p>
     *
     * @param token token JWT extraído del header Authorization
     * @return Claims con subject, email, roles, iat, exp, etc.
     * @throws io.jsonwebtoken.JwtException si el token es inválido, expirado o mal firmado
     */
    public Claims getClaims(String token) {

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Valida si el token JWT es correcto, no ha sido manipulado y no ha expirado.
     * @param token El string del JWT (sin el prefijo "Bearer ")
     * @return true si es válido, false en cualquier otro caso
     */
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
