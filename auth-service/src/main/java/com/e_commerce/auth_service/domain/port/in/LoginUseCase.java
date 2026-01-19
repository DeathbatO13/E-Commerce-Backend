package com.e_commerce.auth_service.domain.port.in;

/**
 * Puerto de entrada (input port) que define el caso de uso principal de autenticación/login.
 * <p>
 * Representa la interfaz que los adaptadores de entrada (como el controlador REST)
 * utilizan para solicitar la autenticación de un usuario.
 * </p>
 * <p>
 * Este puerto pertenece al dominio y es implementado por la capa de aplicación
 * (ej: {@code AuthApplicationService}).
 * </p>
 * <p>
 * Define un contrato simple y explícito para el proceso de login:
 * validar credenciales y retornar un token de acceso si la autenticación es exitosa.
 * </p>
 */
public interface LoginUseCase {

    /**
     * Autentica a un usuario mediante su correo electrónico y contraseña.
     * <p>
     * Si las credenciales son válidas, genera y retorna un token de acceso (normalmente JWT).
     * En caso de error (usuario no existe, contraseña incorrecta, cuenta deshabilitada, etc.),
     * debe lanzar una excepción de dominio o de aplicación apropiada.
     * </p>
     *
     * @param email        correo electrónico del usuario (identificador principal)
     * @param rawPassword  contraseña en texto plano proporcionada por el cliente
     * @return token de acceso válido (String) para utilizar en peticiones posteriores
     * @throws RuntimeException o excepción específica de dominio si la autenticación falla
     */
    String login(String email, String rawPassword);
}
