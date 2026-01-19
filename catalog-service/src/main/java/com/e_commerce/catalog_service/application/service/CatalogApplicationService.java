package com.e_commerce.catalog_service.application.service;

import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.model.Product;
import com.e_commerce.catalog_service.domain.port.in.CreateCategoryUseCase;
import com.e_commerce.catalog_service.domain.port.in.CreateProductUseCase;
import com.e_commerce.catalog_service.domain.port.in.GetCatalogUseCase;
import com.e_commerce.catalog_service.domain.port.out.CatalogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CatalogApplicationService implements CreateCategoryUseCase,
        CreateProductUseCase, GetCatalogUseCase {

    private final CatalogRepository repository;

    public CatalogApplicationService(CatalogRepository repository) {
        this.repository = repository;
    }

    @Override
    public Category create(Category category) {
        return repository.saveCategory(category);
    }

    @Override
    public Product create(Product product) {
        return repository.saveProduct(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return repository.findAllProducts();
    }
}
