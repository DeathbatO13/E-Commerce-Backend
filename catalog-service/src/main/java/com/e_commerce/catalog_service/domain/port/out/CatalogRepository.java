package com.e_commerce.catalog_service.domain.port.out;

import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.model.Product;

import java.util.List;

public interface CatalogRepository {

    Category saveCategory(Category category);

    Product saveProduct(Product product);

    List<Product> findAllProducts();
}
