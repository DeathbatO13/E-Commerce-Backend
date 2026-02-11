package com.e_commerce.catalog_service.adapter.out.persistence.repository;

import com.e_commerce.catalog_service.adapter.out.persistence.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad CategoryJpaEntity.
 * <p>
 * Proporciona operaciones CRUD estándar y consultas personalizadas para categorías,
 * con filtros por estado activo y búsqueda por nombre.
 * </p>
 */
@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, UUID> {

    /**
     * Busca una categoría activa por su nombre exacto.
     *
     * @param name nombre exacto de la categoría
     * @return Optional con la entidad de la categoría si existe y está activa
     */
    Optional<CategoryJpaEntity> findByNameAndActiveTrue(String name);

    /**
     * Obtiene todas las categorías que están activas.
     *
     * @return lista de entidades de categorías activas
     */
    List<CategoryJpaEntity> findByActiveTrue();
}