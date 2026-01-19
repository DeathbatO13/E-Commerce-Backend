package com.e_commerce.auth_service.adapter.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO que contiene las credenciales enviadas por el cliente para iniciar sesión.
 */
public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {
}
