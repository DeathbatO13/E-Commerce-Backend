package com.e_commerce.catalog_service.application.service;


import com.e_commerce.catalog_service.domain.model.Product;
import com.e_commerce.catalog_service.domain.port.in.CreateProductUseCase;
import com.e_commerce.catalog_service.domain.port.in.DeleteProductUseCase;
import com.e_commerce.catalog_service.domain.port.in.ListProductUseCase;
import com.e_commerce.catalog_service.domain.port.in.SearchProductUseCase;
import com.e_commerce.catalog_service.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductApplicationService implements
        CreateProductUseCase, ListProductUseCase,
        SearchProductUseCase, DeleteProductUseCase {


    private final ProductRepository productRepository;

    public ProductApplicationService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Override
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> listAll() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> searchByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public void deleteById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new IllegalArgumentException("Product not found"));

        Product disabled = new Product(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                false
        );

        productRepository.save(disabled);
    }
}
