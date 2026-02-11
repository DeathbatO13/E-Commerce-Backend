package com.e_commerce.catalog_service.adapter.in.rest.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de solicitud utilizado para crear un nuevo producto en el catálogo.
 * <p>
 * Contiene los datos requeridos para registrar un producto a través de la API REST.
 * </p>
 */
public record CreateProductRequest(String name,
                                   String description,
                                   BigDecimal price,
                                   int stock,
                                   UUID categoryId) {
}
