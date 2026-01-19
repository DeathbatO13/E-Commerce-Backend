package com.e_commerce.auth_service.adapter.out.security;

import com.e_commerce.auth_service.domain.port.out.TokenGeneratorPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Adaptador de salida que implementa el puerto {@link TokenGeneratorPort}
 * para la generación de tokens JWT.
 * <p>
 * Utiliza la librería JJWT (io.jsonwebtoken) para crear tokens firmados con HMAC-SHA256.
 * Este componente es el responsable de generar los tokens de acceso que se devuelven
 * al cliente tras un login exitoso.
 * </p>
 * <p>
 * <strong>Importante:</strong> La clave secreta está actualmente hardcodeada.
 * En un entorno de producción debe moverse a variables de entorno, secrets manager
 * o configuración externa segura.
 * </p>
 */
@Component
public class JwtTokenGeneratorAdapter implements TokenGeneratorPort {

    private static final String SECRET = "super-secret-key-super-secret-key";
    private static final long EXPIRATION_MS = 86400000;

    /**
     * Genera un token JWT para el usuario autenticado.
     * <p>
     * El token contiene:
     * <ul>
     *   <li>subject (sub): ID del usuario</li>
     *   <li>claim "email": correo electrónico del usuario</li>
     *   <li>issued at (iat): fecha de emisión</li>
     *   <li>expiration (exp): fecha de expiración (24 horas por defecto)</li>
     * </ul>
     * </p>
     * <p>
     * Firma el token utilizando HMAC-SHA256 con la clave secreta configurada.
     * </p>
     *
     * @param userId ID único del usuario (normalmente UUID como String)
     * @param email  correo electrónico del usuario (se incluye como claim)
     * @return token JWT compactado y firmado (String)
     */
    @Override
    public String generateToken(String userId, String email) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

}
