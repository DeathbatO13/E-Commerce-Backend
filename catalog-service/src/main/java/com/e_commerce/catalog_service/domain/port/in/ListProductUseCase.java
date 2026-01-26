package com.e_commerce.catalog_service.domain.port.in;

import com.e_commerce.catalog_service.domain.model.Product;

import java.util.List;
import java.util.UUID;

public interface ListProductUseCase {
    List<Product> listAll();
    List<Product> listByCategory(UUID categoryId);
    List<Product> listByName(String name);
}
