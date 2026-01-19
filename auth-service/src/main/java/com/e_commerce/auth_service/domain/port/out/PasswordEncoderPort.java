package com.e_commerce.auth_service.domain.port.out;

/**
 * Puerto de salida (output port) que define el contrato para operaciones de codificación y verificación
 * de contraseñas en el dominio.
 * <p>
 * Este puerto aísla la lógica de negocio del dominio de cualquier implementación concreta de
 * codificación de contraseñas (BCrypt, Argon2, PBKDF2, etc.), permitiendo cambiar el algoritmo
 * o la librería sin modificar el núcleo del dominio.
 * </p>
 * <p>
 * Es implementado por adaptadores de salida en la capa de infraestructura
 * (ej: {@code BCryptPasswordEncoderAdapter}).
 * </p>
 */
public interface PasswordEncoderPort {

    /**
     * Codifica (hashea) una contraseña en texto plano.
     * <p>
     * El resultado debe ser un string seguro que incluya:
     * - El algoritmo utilizado
     * - El salt (si aplica)
     * - El hash resultante
     * </p>
     * <p>
     * Nunca se debe almacenar la contraseña original en texto plano.
     * </p>
     *
     * @param rawPassword contraseña en texto plano proporcionada por el usuario
     * @return string con el hash codificado (formato dependiente de la implementación)
     */
    String encode(String rawPassword);

    /**
     * Verifica si una contraseña en texto plano coincide con un hash previamente almacenado.
     * <p>
     * Compara de forma segura la contraseña proporcionada con el hash codificado,
     * teniendo en cuenta el algoritmo, salt y parámetros utilizados en el momento del encoding.
     * </p>
     *
     * @param rawPassword     contraseña en texto plano a verificar
     * @param encodedPassword hash almacenado en la base de datos
     * @return {@code true} si la contraseña coincide, {@code false} en caso contrario
     */
    boolean matches(String rawPassword, String encodedPassword);
}
