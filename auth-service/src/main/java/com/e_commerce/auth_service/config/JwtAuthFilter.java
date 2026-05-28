package com.e_commerce.auth_service.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro de Spring Security que valida y procesa tokens JWT en cada solicitud.
 * <p>
 * Extiende {@link OncePerRequestFilter} para ejecutarse una sola vez por petición.
 * Extrae el token del header "Authorization" (formato Bearer), valida su estructura
 * y firma mediante {@link JwtProvider}, y establece la autenticación en el contexto
 * de seguridad de Spring si el token es válido.
 * </p>
 * <p>
 * Este filtro es parte fundamental del mecanismo de autenticación stateless basado en JWT.
 * No requiere sesión HTTP y permite proteger endpoints con @PreAuthorize o .authenticated().
 * </p>
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter{

    private final JwtProvider jwtProvider;

    public JwtAuthFilter(JwtProvider jwtProvider){
        this.jwtProvider = jwtProvider;
    }

    /**
     * Procesa el filtro para cada solicitud HTTP.
     * <p>
     * - Extrae el token del header Authorization (espera formato "Bearer {token}")<br>
     * - Valida el token y obtiene sus claims<br>
     * - Convierte los roles del claim "roles" en autoridades Spring Security (prefijo ROLE_)<br>
     * - Establece la autenticación en el SecurityContext si el token es válido<br>
     * - Continúa la cadena de filtros independientemente del resultado
     * </p>
     * <p>
     * No lanza excepciones directamente; las validaciones fallidas simplemente
     * dejan el contexto sin autenticar (401/403 manejado por Spring Security).
     * </p>
     *
     * @param request     solicitud HTTP entrante
     * @param response    respuesta HTTP saliente
     * @param filterChain cadena de filtros restante
     * @throws ServletException si ocurre error en el procesamiento del filtro
     * @throws IOException      si ocurre error de entrada/salida
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

            String header = request.getHeader("Authorization");

            if(header != null && header.startsWith("Bearer ")){

                String token = header.substring(7);
                Claims claims = jwtProvider.getClaims(token);

                List<SimpleGrantedAuthority> authorities =
                        ((List<String>) claims.get("roles"))
                                .stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_"+role))
                                .toList();

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(),
                        null,
                        authorities
                );

                SecurityContextHolder.getContext().setAuthentication(auth);

            }

            filterChain.doFilter(request, response);

    }
}
