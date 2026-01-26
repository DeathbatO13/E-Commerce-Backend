package com.e_commerce.catalog_service.domain.port.out;

import com.e_commerce.catalog_service.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(UUID id);

    List<Product> findAll();

    List<Product> findByName(String name);

    List<Product> findByCategory(UUID categoryId);

    void deactivate(UUID id);

    boolean existsByCategory(UUID categoryId);
}
