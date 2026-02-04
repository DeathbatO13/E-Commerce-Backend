package com.e_commerce.catalog_service.adapter.out.persistence;

import com.e_commerce.catalog_service.adapter.out.persistence.entity.CategoryJpaEntity;
import com.e_commerce.catalog_service.adapter.out.persistence.mapper.CategoryMapper;
import com.e_commerce.catalog_service.adapter.out.persistence.repository.CategoryJpaRepository;
import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.port.out.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CategoryAdapter implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;


    public CategoryAdapter(CategoryJpaRepository categoryJpaRepository){
        this.categoryJpaRepository = categoryJpaRepository;
    }


    @Override
    public Category save(Category category) {

        CategoryJpaEntity categoryJpaEntity = categoryJpaRepository
                .findByNameAndActiveTrue(category.getName())
                .orElseThrow(()->new IllegalArgumentException("Category not found"));

        CategoryJpaEntity entity = CategoryMapper.toEntity(category);
        return CategoryMapper.toDomain(categoryJpaRepository.save(entity));
    }

    @Override
    public List<Category> findAll() {
        return categoryJpaRepository.findByActiveTrue()
                .stream().map(CategoryMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return categoryJpaRepository.findById(id)
                .filter(CategoryJpaEntity::isActive)
                .map(CategoryMapper::toDomain);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return categoryJpaRepository.findByNameAndActiveTrue(name)
                .map(CategoryMapper::toDomain);
    }

    @Override
    public void deactivate(UUID id) {
        CategoryJpaEntity entity = categoryJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        entity.setActive(false);
    }
}
