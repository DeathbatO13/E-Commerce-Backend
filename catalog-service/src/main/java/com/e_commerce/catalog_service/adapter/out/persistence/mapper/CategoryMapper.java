package com.e_commerce.catalog_service.adapter.out.persistence.mapper;

import com.e_commerce.catalog_service.adapter.out.persistence.entity.CategoryJpaEntity;
import com.e_commerce.catalog_service.domain.model.Category;

/**
 * Mapper responsable de las conversiones bidireccionales entre
 * el modelo de dominio Category y la entidad JPA CategoryJpaEntity.
 */
public class CategoryMapper {

    /**
     * Convierte un objeto del dominio Category a su representación JPA.
     *
     * @param category modelo de dominio de la categoría
     * @return entidad JPA lista para persistencia
     */
    public static CategoryJpaEntity toEntity(Category category) {
        return new CategoryJpaEntity(
                category.getId(),
                category.getName(),
                category.isActive(),
                null
        );
    }

    /**
     * Convierte una entidad JPA CategoryJpaEntity a su modelo de dominio Category.
     *
     * @param entity entidad JPA de la categoría
     * @return modelo de dominio de la categoría
     */
    public static Category toDomain(CategoryJpaEntity entity) {
        return new Category(
                entity.getId(),
                entity.getName(),
                entity.isActive()
        );
    }
}