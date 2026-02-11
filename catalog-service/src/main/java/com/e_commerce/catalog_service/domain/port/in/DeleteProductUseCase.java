package com.e_commerce.catalog_service.domain.port.in;

import java.util.UUID;

/**
 * Caso de uso de entrada para eliminar (o desactivar) un producto del catálogo.
 * <p>
 * Define el contrato que deben implementar los servicios de aplicación
 * para realizar la eliminación de productos desde el núcleo del dominio.
 * </p>
 */
public interface DeleteProductUseCase {

    /**
     * Elimina (o desactiva lógicamente) un producto por su identificador.
     *
     * @param productId identificador único del producto a eliminar
     */
    void deleteById(UUID productId);
}
