package com.e_commerce.catalog_service.domain.port.in;

import com.e_commerce.catalog_service.domain.model.Product;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso de entrada para consultar y listar productos del catálogo.
 * <p>
 * Define el contrato que deben implementar los servicios de aplicación
 * para exponer operaciones de consulta de productos desde el núcleo del dominio.
 * </p>
 */
public interface ListProductUseCase {

    /**
     * Obtiene la lista de todos los productos activos.
     *
     * @return lista completa de productos
     */
    List<Product> listAll();

    /**
     * Obtiene la lista de productos pertenecientes a una categoría específica.
     *
     * @param categoryId identificador de la categoría
     * @return lista de productos de la categoría indicada
     */
    List<Product> listByCategory(UUID categoryId);

    /**
     * Obtiene la lista de productos cuyo nombre contenga el texto especificado.
     *
     * @param name texto o parte del nombre a buscar
     * @return lista de productos que coinciden con el criterio
     */
    List<Product> listByName(String name);
}
