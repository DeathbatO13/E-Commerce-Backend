package com.e_commerce.auth_service.domain.port.out;

/**
 * Puerto de salida (output port) que define el contrato para la generación
 * de tokens de acceso (normalmente JWT) en el dominio.
 * <p>
 * Este puerto aísla la lógica de negocio del dominio de cualquier implementación concreta
 * de generación de tokens (JWT, opaque tokens, session tokens, etc.), permitiendo cambiar
 * el formato, algoritmo o librería sin modificar el núcleo del dominio.
 * </p>
 * <p>
 * Es implementado por adaptadores de salida en la capa de infraestructura
 * (ej: {@code JwtTokenGeneratorAdapter}).
 * </p>
 */
public interface TokenGeneratorPort {

    /**
     * Genera un token de acceso válido para un usuario autenticado.
     * <p>
     * El token debe contener (como mínimo) la información necesaria para:
     * <ul>
     *   <li>Identificar al usuario en solicitudes posteriores (userId)</li>
     *   <li>Proporcionar datos básicos de contexto (como email)</li>
     *   <li>Tener un tiempo de vida limitado y firma/validación</li>
     * </ul>
     * </p>
     * <p>
     * La implementación concreta decide el formato (JWT recomendado), claims adicionales,
     * algoritmo de firma y tiempo de expiración.
     * </p>
     *
     * @param userId  identificador único del usuario (normalmente UUID como String)
     * @param email   correo electrónico del usuario (usualmente incluido como claim)
     * @return token de acceso en formato String listo para enviar al cliente
     */
    String generateToken(String userId, String email);
}
