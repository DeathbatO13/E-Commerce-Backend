package com.e_commerce.catalog_service.domain.port.in;

import com.e_commerce.catalog_service.domain.model.Product;

import java.util.List;

public interface GetCatalogUseCase {
    List<Product> getAllProducts();
}
