package com.e_commerce.catalog_service.domain.port.in;

import com.e_commerce.catalog_service.domain.model.Category;

import java.util.List;

public interface ListCategoriesUseCase{
    List<Category> listAll();
}
