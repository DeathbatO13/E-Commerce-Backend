package com.e_commerce.catalog_service.adapter.in.rest.dto.response;

import java.util.UUID;


/**
 * DTO de respuesta que representa la información de una categoría devuelta al cliente.
 * <p>
 * Contiene los datos esenciales de la categoría para su exposición en la API REST.
 * </p>
 */
public record CategoryResponse(UUID id, String name, boolean active) {
}
