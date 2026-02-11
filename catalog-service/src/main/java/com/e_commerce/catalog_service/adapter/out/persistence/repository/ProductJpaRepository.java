package com.e_commerce.catalog_service.adapter.out.persistence.repository;

import com.e_commerce.catalog_service.adapter.out.persistence.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad ProductJpaEntity.
 * <p>
 * Proporciona operaciones CRUD estándar y consultas personalizadas para productos,
 * incluyendo filtros por estado activo, categoría y búsqueda por nombre.
 * </p>
 */
@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, UUID> {

    /**
     * Obtiene todos los productos que están activos.
     *
     * @return lista de entidades de productos activos
     */
    List<ProductJpaEntity> findByActiveTrue();

    /**
     * Obtiene todos los productos activos que pertenecen a una categoría específica.
     *
     * @param categoryId identificador de la categoría
     * @return lista de entidades de productos activos de la categoría
     */
    List<ProductJpaEntity> findByCategoryIdAndActiveTrue(UUID categoryId);

    /**
     * Busca productos activos cuyo nombre contenga el texto indicado (insensible a mayúsculas).
     *
     * @param name parte o texto del nombre a buscar
     * @return lista de entidades de productos que coinciden con el criterio
     */
    List<ProductJpaEntity> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    /**
     * Verifica si existen productos activos asociados a una categoría específica.
     *
     * @param categoryId identificador de la categoría
     * @return true si existe al menos un producto activo en la categoría
     */
    boolean existsByCategoryIdAndActiveTrue(UUID categoryId);
}