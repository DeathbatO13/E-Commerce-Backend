package com.e_commerce.catalog_service.adapter.in.rest.dto.request;


/**
 * DTO de solicitud utilizado para actualizar el nombre de una categoría existente.
 * <p>
 * Contiene únicamente el campo modificable en una operación de actualización
 * a través de la API REST.
 * </p>
 */
public record UpdateCategoryRequest(String name) {
}
