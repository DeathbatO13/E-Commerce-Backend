package com.e_commerce.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de beans relacionados con seguridad que son compartidos en toda la aplicación.
 * <p>
 * Principalmente define el {@link PasswordEncoder} que se utilizará para:
 * <ul>
 *   <li>Hashear contraseñas durante el registro de usuarios</li>
 *   <li>Verificar contraseñas durante el proceso de login</li>
 * </ul>
 * </p>
 * <p>
 * Este bean es inyectado automáticamente en componentes que implementan
 * (como {@code BCryptPasswordEncoderAdapter}) o en cualquier lugar donde Spring Security
 * necesite codificar/verificar contraseñas.
 * </p>
 * <p>
 * Se utiliza BCrypt con la configuración por defecto (fuerza/work factor = 10).
 * </p>
 */
@Configuration
public class SecuriryBeansConfig{

    /**
     * Proporciona un {@link BCryptPasswordEncoder} como bean gestionado por Spring.
     * <p>
     * Este encoder es el estándar recomendado por Spring Security para almacenar contraseñas
     * de forma segura (incluye salt aleatorio por contraseña y es resistente a ataques de fuerza bruta).
     * </p>
     *
     * @return instancia de PasswordEncoder configurada con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
