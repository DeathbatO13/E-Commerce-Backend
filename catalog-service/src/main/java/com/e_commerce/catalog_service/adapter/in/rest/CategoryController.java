package com.e_commerce.catalog_service.adapter.in.rest;

import com.e_commerce.catalog_service.adapter.in.rest.dto.request.CreateCategoryRequest;
import com.e_commerce.catalog_service.adapter.in.rest.dto.request.UpdateCategoryRequest;
import com.e_commerce.catalog_service.adapter.in.rest.dto.response.CategoryResponse;
import com.e_commerce.catalog_service.domain.port.in.CreateCategoryUseCase;
import com.e_commerce.catalog_service.domain.port.in.DeleteCategoryUseCase;
import com.e_commerce.catalog_service.domain.port.in.ListCategoriesUseCase;
import com.e_commerce.catalog_service.domain.port.in.UpdateCategoryUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    public CategoryController(
            CreateCategoryUseCase createCategoryUseCase,
            ListCategoriesUseCase listCategoriesUseCase,
            UpdateCategoryUseCase updateCategoryUseCase,
            DeleteCategoryUseCase deleteCategoryUseCase
    ){

        this.createCategoryUseCase = createCategoryUseCase;
        this.listCategoriesUseCase = listCategoriesUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
    }

    @PostMapping
    public CategoryResponse create(@RequestBody CreateCategoryRequest request){
        var category = createCategoryUseCase.create(request.name());
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.isActive()
        );
    }


    @GetMapping
    public List<CategoryResponse> list(){
        return listCategoriesUseCase.listAll()
                .stream()
                .map(c -> new CategoryResponse(
                        c.getId(),
                        c.getName(),
                        c.isActive()
                )).toList();
    }

    @PutMapping("/{id}")
    public CategoryResponse update(
            @PathVariable UUID id,
            @RequestBody UpdateCategoryRequest request
    ) {
        var category = updateCategoryUseCase.update(id, request.name());
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.isActive()
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        deleteCategoryUseCase.deleteById(id);
    }

}
