package com.e_commerce.catalog_service.adapter.out.persistence;

import com.e_commerce.catalog_service.adapter.out.persistence.entity.CategoryJpaEntity;
import com.e_commerce.catalog_service.adapter.out.persistence.entity.ProductJpaEntity;
import com.e_commerce.catalog_service.adapter.out.persistence.mapper.ProductMapper;
import com.e_commerce.catalog_service.adapter.out.persistence.repository.CategoryJpaRepository;
import com.e_commerce.catalog_service.adapter.out.persistence.repository.ProductJpaRepository;
import com.e_commerce.catalog_service.domain.model.Product;
import com.e_commerce.catalog_service.domain.port.out.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;

    public ProductAdapter(ProductJpaRepository productJpaRepository, CategoryJpaRepository categoryJpaRepository){
        this.productJpaRepository = productJpaRepository;
        this.categoryJpaRepository = categoryJpaRepository;
    }

    @Override
    public Product save(Product product) {

        CategoryJpaEntity categoryJpaEntity = categoryJpaRepository
                .findById(product.getId())
                .orElseThrow(()->new IllegalArgumentException("Category not found"));

        ProductJpaEntity entity = ProductMapper.toEntity(product);
        return ProductMapper.toDomain(productJpaRepository.save(entity));
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productJpaRepository.findById(id)
                .filter(ProductJpaEntity::isActive)
                .map(ProductMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findByActiveTrue()
                .stream()
                .map(ProductMapper::toDomain)
                .toList();
    }

    @Override
    public List<Product> findByName(String name) {
        return productJpaRepository
                .findByNameContainingIgnoreCaseAndActiveTrue(name)
                .stream()
                .map(ProductMapper::toDomain)
                .toList();
    }

    @Override
    public List<Product> findByCategory(UUID categoryId) {
        return productJpaRepository
                .findByCategoryIdAndActiveTrue(categoryId)
                .stream().map(ProductMapper::toDomain)
                .toList();
    }

    @Override
    public void deactivate(UUID id) {
        ProductJpaEntity entity = productJpaRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Product not found"));
        entity.setActive(false);
    }

    @Override
    public boolean existsByCategory(UUID categoryId) {
        return productJpaRepository.existsByCategoryIdAndActiveTrue(categoryId);
    }
}
