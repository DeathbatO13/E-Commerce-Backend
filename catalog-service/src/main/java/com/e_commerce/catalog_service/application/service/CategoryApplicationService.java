package com.e_commerce.catalog_service.application.service;

import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.port.in.CreateCategoryUseCase;
import com.e_commerce.catalog_service.domain.port.in.DeleteCategoryUseCase;
import com.e_commerce.catalog_service.domain.port.in.ListCategoriesUseCase;
import com.e_commerce.catalog_service.domain.port.out.CategoryRepository;

import java.util.List;
import java.util.UUID;

public class CategoryApplicationService implements CreateCategoryUseCase,
        ListCategoriesUseCase, DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public CategoryApplicationService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteById(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new IllegalArgumentException("Category not found"));

        Category disabled = new Category(
                category.getId(),
                category.getName(),
                category.isActive()
        );

        categoryRepository.save(disabled);
    }

    @Override
    public List<Category> listAll() {
        return categoryRepository.findAll();
    }
}
