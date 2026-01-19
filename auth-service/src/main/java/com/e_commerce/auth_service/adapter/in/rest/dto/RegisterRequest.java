package com.e_commerce.auth_service.adapter.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO que contiene los datos enviados por el cliente para realizar el registro.
 */
public record RegisterRequest(
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String fullname
) {
}
