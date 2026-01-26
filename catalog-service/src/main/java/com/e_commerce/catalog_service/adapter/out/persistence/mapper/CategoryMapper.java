package com.e_commerce.catalog_service.adapter.out.persistence.mapper;


import com.e_commerce.catalog_service.adapter.out.persistence.entity.CategoryJpaEntity;
import com.e_commerce.catalog_service.domain.model.Category;


public class CategoryMapper {

    public static CategoryJpaEntity toEntity(Category category) {
        return new CategoryJpaEntity(
                category.getId(),
                category.getName(),
                category.isActive(),
                null
        );
    }

    public static Category toDomain(CategoryJpaEntity entity) {
        return new Category(
                entity.getId(),
                entity.getName(),
                entity.isActive()
        );
    }
}
