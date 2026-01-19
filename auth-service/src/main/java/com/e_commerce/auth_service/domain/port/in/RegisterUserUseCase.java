package com.e_commerce.auth_service.domain.port.in;

import com.e_commerce.auth_service.domain.model.User;

/**
 * Puerto de entrada (input port) que define el caso de uso principal de registro de usuarios.
 * <p>
 * Representa el contrato que los adaptadores de entrada (como el controlador REST)
 * utilizan para solicitar la creación de un nuevo usuario en el sistema.
 * </p>
 * <p>
 * Este puerto pertenece al dominio y es implementado por la capa de aplicación
 * (ej: {@code AuthApplicationService}).
 * </p>
 * <p>
 * Define un flujo simple y explícito para el registro:
 * validar datos, codificar contraseña, asignar rol por defecto y persistir el usuario.
 * </p>
 */
public interface RegisterUserUseCase {

    /**
     * Registra un nuevo usuario en el sistema.
     * <p>
     * Responsabilidades principales (implementadas por la capa de aplicación):
     * <ul>
     *   <li>Verificar que el email no esté ya registrado</li>
     *   <li>Codificar la contraseña en texto plano</li>
     *   <li>Asignar rol(es) por defecto (normalmente CLIENT)</li>
     *   <li>Crear y persistir el usuario</li>
     *   <li>Retornar la entidad completa del usuario recién creado</li>
     * </ul>
     * </p>
     * <p>
     * En caso de violación de reglas de negocio (email duplicado, datos inválidos, etc.),
     * debe lanzar una excepción de dominio o de aplicación apropiada.
     * </p>
     *
     * @param email        correo electrónico del nuevo usuario (debe ser único)
     * @param rawPassword  contraseña en texto plano proporcionada por el cliente
     * @param fullname     nombre completo del usuario
     * @return la entidad {@link User} recién creada y persistida
     * @throws RuntimeException o excepción específica de dominio si el registro falla
     */
    User register(String email, String rawPassword, String fullname);
}
