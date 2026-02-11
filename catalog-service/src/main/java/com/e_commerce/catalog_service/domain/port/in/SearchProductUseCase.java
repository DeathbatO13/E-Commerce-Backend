package com.e_commerce.catalog_service.domain.port.in;

import com.e_commerce.catalog_service.domain.model.Product;

import java.util.List;

/**
 * Caso de uso de entrada para buscar productos por nombre en el catálogo.
 * <p>
 * Define el contrato que deben implementar los servicios de aplicación
 * para realizar búsquedas de productos desde el núcleo del dominio.
 * </p>
 */
public interface SearchProductUseCase {

    /**
     * Busca productos cuyo nombre contenga el texto especificado (búsqueda parcial).
     *
     * @param name texto o parte del nombre del producto a buscar
     * @return lista de productos que coinciden con el criterio de búsqueda
     */
    List<Product> searchByName(String name);
}