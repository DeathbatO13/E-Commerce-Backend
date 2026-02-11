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

/**
 * Controlador REST que expone los endpoints para la gestión de productos.
 * <p>
 * Implementa las operaciones CRUD del catálogo de productos mediante los casos de uso definidos
 * en el dominio, transformando las solicitudes y respuestas entre DTOs y modelos de dominio.
 * </p>
 */
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

    /**
     * Convierte un modelo de dominio Product a su representación en respuesta REST.
     *
     * @param product producto del dominio
     * @return DTO de respuesta para el cliente
     */
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

    /**
     * Crea un nuevo producto en el catálogo.
     *
     * @param request DTO con los datos del producto a crear
     * @return respuesta con los datos del producto creado
     */
    @PostMapping
    public ProductResponse create(@RequestBody CreateProductRequest request) {
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

    /**
     * Lista productos con filtros opcionales por categoría o nombre.
     * <p>
     * Si se proporciona categoryId, filtra por categoría.<br>
     * Si se proporciona name, filtra por nombre.<br>
     * Si no se proporciona ninguno, retorna todos los productos.
     * </p>
     *
     * @param categoryId (opcional) identificador de categoría
     * @param name (opcional) texto de búsqueda por nombre
     * @return lista de productos en formato de respuesta
     */
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

    /**
     * Obtiene los detalles de un producto por su identificador.
     *
     * @param id identificador único del producto
     * @return respuesta con los datos del producto
     */
    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable UUID id) {
        return toResponse(getProductUseCase.getById(id));
    }

    /**
     * Actualiza los datos de un producto existente.
     *
     * @param id identificador del producto a actualizar
     * @param request DTO con los datos actualizados
     * @return respuesta con el producto actualizado
     */
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

    /**
     * Elimina (desactiva lógicamente) un producto por su identificador.
     *
     * @param id identificador del producto a eliminar
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        deleteProductUseCase.deleteById(id);
    }
}