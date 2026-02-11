package com.e_commerce.catalog_service.adapter.in.rest.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de respuesta que representa la información de un producto devuelta al cliente.
 * <p>
 * Contiene los datos esenciales del producto para su exposición en la API REST.
 * </p>
 */

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        int stock,
        UUID categoryId,
        boolean active
) {
}
