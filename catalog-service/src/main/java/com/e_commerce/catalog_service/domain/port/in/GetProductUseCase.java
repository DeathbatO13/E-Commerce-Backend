package com.e_commerce.catalog_service.domain.port.in;

import com.e_commerce.catalog_service.domain.model.Product;

import java.util.UUID;

/**
 * Caso de uso de entrada para obtener un producto específico por su identificador.
 * <p>
 * Define el contrato que deben implementar los servicios de aplicación
 * para consultar un producto individual desde el núcleo del dominio.
 * </p>
 */
public interface GetProductUseCase {

    /**
     * Obtiene un producto por su identificador único.
     *
     * @param uuid identificador del producto a consultar
     * @return el producto encontrado
     */
    Product getById(UUID uuid);
}
