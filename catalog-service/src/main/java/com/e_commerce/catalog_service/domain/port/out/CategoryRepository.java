package com.e_commerce.catalog_service.domain.port.out;

import com.e_commerce.catalog_service.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para operaciones de persistencia y consulta de categorías.
 * Define el contrato que deben implementar los adaptadores de infraestructura.
 */
public interface CategoryRepository {

    /**
     * Guarda o actualiza una categoría.
     *
     * @param category la categoría a persistir
     * @return la categoría guardada
     */
    Category save(Category category);

    /**
     * Obtiene todas las categorías.
     *
     * @return lista de categorías
     */
    List<Category> findAll();

    /**
     * Busca una categoría por su identificador.
     *
     * @param id identificador de la categoría
     * @return Optional con la categoría si existe
     */
    Optional<Category> findById(UUID id);

    /**
     * Busca una categoría por su nombre exacto.
     *
     * @param name nombre de la categoría
     * @return Optional con la categoría si existe
     */
    Optional<Category> findByName(String name);

    /**
     * Desactiva (eliminación lógica) una categoría.
     *
     * @param id identificador de la categoría a desactivar
     */
    void deactivate(UUID id);

    boolean existsById(UUID id);
}

