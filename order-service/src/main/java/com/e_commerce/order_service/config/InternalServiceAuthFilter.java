package com.e_commerce.order_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

/**
 * Autentica llamadas entre microservicios (por ejemplo, payment-service → POST /orders/{id}/pay).
 * <p>
 * Si el header {@value #HEADER} coincide con el token interno configurado, la petición se
 * autentica con el rol {@code SERVICE}. Ese rol nunca lo emite auth-service, así que un
 * usuario final no puede obtenerlo con su JWT.
 * </p>
 */
@Component
public class InternalServiceAuthFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Internal-Token";
    public static final String ROLE = "SERVICE";

    private final byte[] internalToken;

    public InternalServiceAuthFilter(@Value("${security.internal-token:}") String internalToken) {
        this.internalToken = internalToken.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HEADER);

        // Comparación en tiempo constante para no filtrar el token por timing
        if (header != null && internalToken.length > 0 &&
                MessageDigest.isEqual(internalToken, header.getBytes(StandardCharsets.UTF_8))) {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    "internal-service",
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + ROLE))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
