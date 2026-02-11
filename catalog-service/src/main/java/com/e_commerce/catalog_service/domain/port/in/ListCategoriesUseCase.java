package com.e_commerce.catalog_service.domain.port.in;

import com.e_commerce.catalog_service.domain.model.Category;

import java.util.List;

/**
 * Caso de uso de entrada para consultar y listar categorías del catálogo.
 * <p>
 * Define el contrato que deben implementar los servicios de aplicación
 * para exponer operaciones de consulta de categorías desde el núcleo del dominio.
 * </p>
 */
public interface ListCategoriesUseCase {

    /**
     * Obtiene la lista de todas las categorías activas.
     *
     * @return lista completa de categorías
     */
    List<Category> listAll();
}