package com.e_commerce.catalog_service.adapter.out.persistence.mapper;

import com.e_commerce.catalog_service.adapter.out.persistence.entity.CategoryJpaEntity;
import com.e_commerce.catalog_service.adapter.out.persistence.entity.ProductJpaEntity;
import com.e_commerce.catalog_service.domain.model.Category;

import java.util.List;

public class CategoryMapper {

    public static CategoryJpaEntity toEntity(Category category, List<ProductJpaEntity> products){
        return new CategoryJpaEntity(
                category.getId(),
                category.getName(),
                category.isActive(),
                products
        );
    }

    public static Category toDomain(CategoryJpaEntity entity){
        return new Category(
                entity.getId(),
                entity.getName(),
                entity.isActive()
        );
    }
}
