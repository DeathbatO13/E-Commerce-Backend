package com.e_commerce.auth_service.adapter.out.security;

import com.e_commerce.auth_service.domain.model.Role;
import com.e_commerce.auth_service.domain.model.User;
import com.e_commerce.auth_service.domain.port.out.TokenGeneratorPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Value("${security.jwt.secret}")
    private String secretKey;
    @Value("${security.jwt.expiration}")
    private long expiration;

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
     * @param user usuario
     * @return token JWT compactado y firmado (String)
     */
    @Override
    public String generateToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles().stream().map(Role::name).toList());

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder().setClaims(claims)
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}
