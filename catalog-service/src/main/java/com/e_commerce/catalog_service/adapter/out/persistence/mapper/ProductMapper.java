package com.e_commerce.catalog_service.adapter.out.persistence.mapper;

import com.e_commerce.catalog_service.adapter.out.persistence.entity.ProductJpaEntity;
import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.model.Product;

/**
 * Mapper responsable de las conversiones bidireccionales entre
 * el modelo de dominio Product y la entidad JPA ProductJpaEntity.
 */
public class ProductMapper {

    /**
     * Convierte un objeto del dominio Product a su representación JPA.
     * <p>
     * No mapea la relación con la categoría (se establece posteriormente).
     * </p>
     *
     * @param product modelo de dominio del producto
     * @return entidad JPA lista para persistencia
     */
    public static ProductJpaEntity toEntity(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setId(product.getId());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setStock(product.getStock());
        entity.setActive(product.isActive());
        return entity;
    }

    /**
     * Convierte una entidad JPA ProductJpaEntity a su modelo de dominio Product.
     * <p>
     * Incluye la conversión de la categoría asociada si existe.
     * </p>
     *
     * @param entity entidad JPA del producto
     * @return modelo de dominio del producto, o null si la entidad es null
     */
    public static Product toDomain(ProductJpaEntity entity) {
        if (entity == null) return null;

        Category domainCategory = null;
        if (entity.getCategory() != null) {
            domainCategory = new Category(
                    entity.getCategory().getId(),
                    entity.getCategory().getName(),
                    entity.getCategory().isActive()
            );
        }

        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStock(),
                domainCategory,
                entity.isActive()
        );
    }
}