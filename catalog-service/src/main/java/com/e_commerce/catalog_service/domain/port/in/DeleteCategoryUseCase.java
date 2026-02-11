package com.e_commerce.catalog_service.domain.port.in;

import java.util.UUID;

/**
 * Caso de uso de entrada para eliminar (o desactivar) una categoría del catálogo.
 * <p>
 * Define el contrato que deben implementar los servicios de aplicación
 * para realizar la eliminación de categorías desde el núcleo del dominio.
 * </p>
 */
public interface DeleteCategoryUseCase {

    /**
     * Elimina (o desactiva lógicamente) una categoría por su identificador.
     *
     * @param categoryId identificador único de la categoría a eliminar
     */
    void deleteById(UUID categoryId);
}
