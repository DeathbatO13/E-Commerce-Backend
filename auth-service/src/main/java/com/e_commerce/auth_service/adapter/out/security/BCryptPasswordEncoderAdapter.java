package com.e_commerce.auth_service.adapter.out.security;

import com.e_commerce.auth_service.domain.port.out.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida que implementa el puerto {@link PasswordEncoderPort}
 * utilizando BCrypt como algoritmo de hash de contraseñas.
 * <p>
 * Encapsula la implementación concreta de {@link BCryptPasswordEncoder} de Spring Security,
 * permitiendo que el dominio dependa únicamente del puerto sin conocer detalles de la librería.
 * </p>
 * <p>
 * Este componente es el encargado de:
 * <ul>
 *   <li>Hashear contraseñas nuevas durante el registro</li>
 *   <li>Verificar contraseñas durante el login</li>
 * </ul>
 * </p>
 */
@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort{

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * * Codifica (hashea) una contraseña en texto plano utilizando BCrypt.
     * *
     * * @param rawPassword la contraseña en texto plano
     * * @return hash BCrypt seguro y con salt incluido
     *
     */
    @Override
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * Verifica si una contraseña en texto plano coincide con su hash almacenado.
     *
     * @param rawPassword    contraseña proporcionada por el usuario (texto plano)
     * @param encodedPassword hash almacenado en la base de datos
     * @return true si la contraseña coincide, false en caso contrario
     */
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
