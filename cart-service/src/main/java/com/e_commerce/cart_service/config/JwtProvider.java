package com.e_commerce.cart_service.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    //@Value("${security.jwt.secret}")
    private String secret;

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
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


    public long extractUserId(String token){
        return getClaims(token).get("userId", Long.class);
    }

    public String extractEmail(String token){
        return getClaims(token).getSubject();
    }
}
