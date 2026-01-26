package com.e_commerce.catalog_service.application.service;


import com.e_commerce.catalog_service.domain.model.Product;
import com.e_commerce.catalog_service.domain.port.in.*;
import com.e_commerce.catalog_service.domain.port.out.CategoryRepository;
import com.e_commerce.catalog_service.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductApplicationService implements
        CreateProductUseCase, ListProductUseCase, SearchProductUseCase,
        DeleteProductUseCase, UpdateProductUseCase, GetProductUseCase {


    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductApplicationService(ProductRepository productRepository,
                                     CategoryRepository categoryRepository){

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Product create(Product product) {
        if (!categoryRepository.findById(product.getCategory().getId()).isPresent()) {
            throw new IllegalArgumentException("Category not found");
        }
        return productRepository.save(product);
    }

    @Override
    public List<Product> listAll() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> listByCategory(UUID categoryId) {
        return productRepository.findByCategory(categoryId);
    }

    @Override
    public List<Product> listByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> searchByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public void deleteById(UUID productId) {
        productRepository.deactivate(productId);
    }

    @Override
    public Product update(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product getById(UUID uuid) {
        return productRepository.findById(uuid)
                .orElseThrow(()->new IllegalArgumentException("Product not found"));
    }
}
