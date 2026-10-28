package com.e_commerce.catalog_service.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

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
                                   @NotNull @Positive BigDecimal price,
                                   @PositiveOrZero int stock,
                                   @NotNull UUID categoryId) {
}
