package com.e_commerce.catalog_service.adapter.out.persistence.mapper;
import com.e_commerce.catalog_service.adapter.out.persistence.entity.ProductJpaEntity;
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
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStock(),
                null,
                entity.isActive()
        );
    }
}
