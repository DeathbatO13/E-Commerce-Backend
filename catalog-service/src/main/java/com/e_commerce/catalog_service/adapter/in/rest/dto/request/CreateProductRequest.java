package com.e_commerce.catalog_service.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de solicitud utilizado para crear un nuevo producto en el catálogo.
 * <p>
 * Contiene los datos requeridos para registrar un producto a través de la API REST.
 * </p>
 */
public record CreateProductRequest(@NotBlank String name,
                                   @NotBlank String description,
                                   @NotBlank BigDecimal price,
                                   @NotBlank int stock,
                                   @NotBlank UUID categoryId) {
}
