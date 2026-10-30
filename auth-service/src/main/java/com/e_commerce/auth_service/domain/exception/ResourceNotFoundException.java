package com.e_commerce.auth_service.domain.exception;

/**
 * Se lanza cuando el recurso pedido no existe. GlobalExceptionHandler la traduce a 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
