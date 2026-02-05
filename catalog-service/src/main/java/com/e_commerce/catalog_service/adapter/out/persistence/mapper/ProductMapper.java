package com.e_commerce.catalog_service.adapter.out.persistence.mapper;
import com.e_commerce.catalog_service.adapter.out.persistence.entity.ProductJpaEntity;
import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.model.Product;

public class ProductMapper {

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
