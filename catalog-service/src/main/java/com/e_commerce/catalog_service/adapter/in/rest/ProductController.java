package com.e_commerce.catalog_service.adapter.in.rest;


import com.e_commerce.catalog_service.adapter.in.rest.dto.request.CreateProductRequest;
import com.e_commerce.catalog_service.adapter.in.rest.dto.request.UpdateProductRequest;
import com.e_commerce.catalog_service.adapter.in.rest.dto.response.ProductResponse;
import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.model.Product;
import com.e_commerce.catalog_service.domain.port.in.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final ListProductUseCase listProductsUseCase;
    private final GetProductUseCase getProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;


    public ProductController(
            CreateProductUseCase createProductUseCase,
            ListProductUseCase listProductsUseCase,
            GetProductUseCase getProductUseCase,
            UpdateProductUseCase updateProductUseCase,
            DeleteProductUseCase deleteProductUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.listProductsUseCase = listProductsUseCase;
        this.getProductUseCase = getProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory().getId(),
                product.isActive()
        );
    }


    @PostMapping
    public ProductResponse create(@RequestBody CreateProductRequest request){
        var product = createProductUseCase.create(
                new Product(
                        UUID.randomUUID(),
                        request.name(),
                        request.description(),
                        request.price(),
                        request.stock(),
                        new Category(request.categoryId(), null, true),
                        true
                )
        );

        return toResponse(product);
    }


    @GetMapping
    public List<ProductResponse> list(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String name
    ) {
        var products =
                categoryId != null ? listProductsUseCase.listByCategory(categoryId)
                        : name != null ? listProductsUseCase.listByName(name)
                        : listProductsUseCase.listAll();

        return products.stream().map(this::toResponse).toList();
    }


    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable UUID id) {
        return toResponse(getProductUseCase.getById(id));
    }


    @PutMapping("/{id}")
    public ProductResponse update(
            @PathVariable UUID id,
            @RequestBody UpdateProductRequest request
    ) {
        var product = updateProductUseCase.update(
                new Product(
                        id,
                        request.name(),
                        request.description(),
                        request.price(),
                        request.stock(),
                        new Category(request.categoryId(), null, true),
                        request.active()
                )
        );

        return toResponse(product);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        deleteProductUseCase.deleteById(id);
    }


}
