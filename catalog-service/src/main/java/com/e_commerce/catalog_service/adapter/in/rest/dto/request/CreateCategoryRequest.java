package com.e_commerce.catalog_service.adapter.in.rest.dto.request;


/**
 * DTO de solicitud utilizado para crear una nueva categoría en el catálogo.
 * <p>
 * Contiene el nombre requerido para registrar una categoría a través de la API REST.
 * </p>
 */
public record CreateCategoryRequest(String name) {
}
