package com.e_commerce.catalog_service.domain.port.in;

import com.e_commerce.catalog_service.domain.model.Product;

/**
 * Caso de uso de entrada para crear un nuevo producto en el catálogo.
 * <p>
 * Define el contrato que deben implementar los servicios de aplicación
 * para realizar la creación de productos desde el núcleo del dominio.
 * </p>
 */
public interface CreateProductUseCase {

    /**
     * Crea un nuevo producto en el catálogo.
     *
     * @param product los datos del producto a crear
     * @return el producto creado (con ID generado y estado inicial)
     */
    Product create(Product product);
}
