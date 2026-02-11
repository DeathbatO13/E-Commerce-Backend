package com.e_commerce.catalog_service.domain.port.in;

import com.e_commerce.catalog_service.domain.model.Category;

import java.util.UUID;
/**
 * Caso de uso de entrada para actualizar una categoría existente en el catálogo.
 * <p>
 * Define el contrato que deben implementar los servicios de aplicación
 * para realizar la actualización de categorías desde el núcleo del dominio.
 * </p>
 */
public interface UpdateCategoryUseCase {

    /**
     * Actualiza los datos de una categoría existente.
     *
     * @param uuid identificador único de la categoría a actualizar
     * @param name nuevo nombre de la categoría
     * @return la categoría actualizada tras aplicar los cambios
     */
    Category update(UUID uuid, String name);
}