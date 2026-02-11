package com.e_commerce.catalog_service.domain.port.out;

import com.e_commerce.catalog_service.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para operaciones de persistencia y consulta de productos.
 * Define el contrato que deben cumplir los adaptadores de infraestructura.
 */
public interface ProductRepository {

    /**
     * Guarda o actualiza un producto.
     *
     * @param product el producto a persistir
     * @return el producto guardado
     */
    Product save(Product product);

    /**
     * Busca un producto por su identificador.
     *
     * @param id identificador del producto
     * @return Optional con el producto si existe
     */
    Optional<Product> findById(UUID id);

    /**
     * Obtiene todos los productos.
     *
     * @return lista de productos
     */
    List<Product> findAll();

    /**
     * Busca productos por nombre (coincidencia parcial).
     *
     * @param name texto de búsqueda en el nombre
     * @return lista de productos que coinciden
     */
    List<Product> findByName(String name);

    /**
     * Obtiene los productos de una categoría específica.
     *
     * @param categoryId id de la categoría
     * @return lista de productos de la categoría
     */
    List<Product> findByCategory(UUID categoryId);

    /**
     * Desactiva (eliminación lógica) un producto.
     *
     * @param id identificador del producto a desactivar
     */
    void deactivate(UUID id);

    /**
     * Verifica si existen productos asociados a una categoría.
     *
     * @param categoryId id de la categoría
     * @return true si hay productos en la categoría
     */
    boolean existsByCategory(UUID categoryId);
}