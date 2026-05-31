package com.e_commerce.auth_service.adapter.in.rest.dto;

/**
 * Respuesta del endpoint de login con el token de autenticación.
 */
public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn) {
}
