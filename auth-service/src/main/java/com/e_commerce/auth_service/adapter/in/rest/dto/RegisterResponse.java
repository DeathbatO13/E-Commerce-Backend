package com.e_commerce.auth_service.adapter.in.rest.dto;

import java.util.UUID;


/**
 * Respuesta del endpoint de register la id y el email del usuario registrado.
 */
public record RegisterResponse(UUID id, String email){
}
