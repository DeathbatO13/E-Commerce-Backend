package com.e_commerce.catalog_service.domain.port.in;

import com.e_commerce.catalog_service.domain.model.Product;
/**
 * Caso de uso de entrada para actualizar un producto existente en el catálogo.
 * <p>
 * Define el contrato que deben implementar los servicios de aplicación
 * para realizar la actualización de productos desde el núcleo del dominio.
 * </p>
 */
public interface UpdateProductUseCase {

    /**
     * Actualiza los datos de un producto existente.
     *
     * @param product el producto con los datos actualizados
     * @return el producto actualizado tras aplicar los cambios
     */
    Product update(Product product);
}
