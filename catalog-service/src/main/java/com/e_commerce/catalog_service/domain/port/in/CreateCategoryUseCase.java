package com.e_commerce.catalog_service.domain.port.in;

import com.e_commerce.catalog_service.domain.model.Category;

/**
 * Caso de uso de entrada para crear una nueva categoría en el catálogo.
 * <p>
 * Define el contrato que deben implementar los servicios de aplicación
 * para realizar la creación de categorías desde el núcleo del dominio.
 * </p>
 */
public interface CreateCategoryUseCase {

    /**
     * Crea una nueva categoría en el catálogo.
     *
     * @param name nombre de la categoría a crear
     * @return la categoría creada (con ID generado y estado inicial)
     */
    Category create(String name);
}
