package com.e_commerce.catalog_service.adapter.in.rest.dto.request;

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
        String name,
        String description,
        BigDecimal price,
        int stock,
        UUID categoryId,
        boolean active
) {
}
