package com.e_commerce.auth_service.config;

import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Configuración principal de Spring Security para la aplicación.
 * <p>
 * Define la cadena de filtros de seguridad (SecurityFilterChain) con las siguientes características:
 * <ul>
 *   <li>Autenticación basada en JWT (stateless - sin sesiones HTTP)</li>
 *   <li>Deshabilitación de CSRF (no necesario en APIs REST con JWT)</li>
 *   <li>Autorización basada en rutas (endpoints públicos vs protegidos)</li>
 *   <li>Inserción del filtro personalizado {@link JwtAuthFilter} antes del filtro de autenticación por defecto</li>
 *   <li>Habilitación de seguridad a nivel de métodos con @PreAuthorize, @Secured, etc.</li>
 * </ul>
 * </p>
 * <p>
 * Esta configuración convierte la aplicación en una API REST segura y stateless.
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Define y configura la cadena principal de filtros de seguridad de Spring Security.
     * <p>
     * Características clave configuradas:
     * <ul>
     *   <li>CSRF desactivado (API REST + JWT)</li>
     *   <li>Sin gestión de sesiones (STATELESS)</li>
     *   <li>Endpoints públicos: /auth/** (login, register), Swagger UI y OpenAPI docs</li>
     *   <li>Todas las demás rutas requieren autenticación</li>
     *   <li>Adición del filtro JwtAuthFilter antes del UsernamePasswordAuthenticationFilter</li>
     * </ul>
     * </p>
     *
     * @param http objeto de configuración HttpSecurity
     * @return la cadena de filtros de seguridad completa
     * @throws Exception si ocurre algún error durante la configuración
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // Usa el bean CorsConfigurationSource de CorsConfig
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Sin token (o con token inválido) → 401; con token pero sin el rol → 403
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        // changeRole exige además SUPER_ADMIN con @PreAuthorize en UserController
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("ADMIN", "CLIENT")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
