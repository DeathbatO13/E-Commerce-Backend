package com.e_commerce.auth_service.config;

import com.e_commerce.auth_service.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static java.util.stream.Collectors.toList;

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

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration}")
    private long expiration;

    /**
     * Genera un token JWT firmado para el usuario autenticado.
     * <p>
     * Incluye los siguientes claims:
     * <ul>
     *   <li>sub (subject): ID del usuario como String</li>
     *   <li>email: correo electrónico</li>
     *   <li>roles: lista de nombres de roles (ej: ["CLIENT", "ADMIN"])</li>
     *   <li>iat: fecha de emisión</li>
     *   <li>exp: fecha de expiración</li>
     * </ul>
     * </p>
     * <p>
     * La firma se realiza con HMAC-SHA256 usando la clave secreta configurada.
     * </p>
     *
     * @param user usuario autenticado con sus datos (id, email, roles)
     * @return token JWT compactado y firmado
     */
    public String generarToken(User user){
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("roles", user.getRoles()
                        .stream()
                        .map(Enum::name)
                        .toList()
                )
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

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
        return Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
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


    public long extractUserId(String token){
        return getClaims(token).get("userId", Long.class);
    }

    public String extractEmail(String token){
        return getClaims(token).getSubject();
    }
}
