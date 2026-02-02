package com.e_commerce.catalog_service.application.service;

import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.port.in.CreateCategoryUseCase;
import com.e_commerce.catalog_service.domain.port.in.DeleteCategoryUseCase;
import com.e_commerce.catalog_service.domain.port.in.ListCategoriesUseCase;
import com.e_commerce.catalog_service.domain.port.in.UpdateCategoryUseCase;
import com.e_commerce.catalog_service.domain.port.out.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CategoryApplicationService implements CreateCategoryUseCase,
        ListCategoriesUseCase, UpdateCategoryUseCase ,DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public CategoryApplicationService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category create(String name) {
        if (categoryRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Category already exists");
        }
        return categoryRepository.save(
                new Category(UUID.randomUUID(), name, true));
    }

    @Override
    public void deleteById(UUID categoryId) {
        categoryRepository.deactivate(categoryId);
    }

    @Override
    public List<Category> listAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category update(UUID uuid, String name) {
        Category category = categoryRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        category.setName(name);
        return categoryRepository.save(category);
    }
}
