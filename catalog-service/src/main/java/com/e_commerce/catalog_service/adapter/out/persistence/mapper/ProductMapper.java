package com.e_commerce.catalog_service.adapter.out.persistence.mapper;

import com.e_commerce.catalog_service.adapter.out.persistence.entity.CategoryJpaEntity;
import com.e_commerce.catalog_service.adapter.out.persistence.entity.ProductJpaEntity;
import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.model.Product;

public class ProductMapper {

    public static ProductJpaEntity toEntity(Product product, CategoryJpaEntity categoryJpaEntity){
        return new ProductJpaEntity(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                categoryJpaEntity,
                product.isActive()
        );
    }

    public static Product toDomain(ProductJpaEntity entity, Category category){
        return new Product(
                entity.getId(),
                entity.getNombre(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStock(),
                category,
                entity.isActive()
        );
    }
}
