package com.e_commerce.auth_service.adapter.in.rest.dto;

import java.util.List;

/**
 * Respuesta del endpoint de usuarios, mostrando la id, email, fullname, y role.
 */
public record UserResponse(
        String id,
        String email,
        List<String> roles,
        boolean enabled) {
}
