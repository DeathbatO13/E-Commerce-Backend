package com.e_commerce.catalog_service.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.UUID;


/**
 * DTO de solicitud utilizado para actualizar los datos de un producto existente.
 * <p>
 * Contiene los campos que se pueden modificar en una operación de actualización
 * a través de la API REST.
 * </p>
 */
public record UpdateProductRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank BigDecimal price,
        @NotBlank int stock,
        @NotBlank UUID categoryId,
        boolean active
) {
}
