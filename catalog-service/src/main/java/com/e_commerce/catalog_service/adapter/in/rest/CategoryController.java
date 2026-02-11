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

/**
 * Controlador REST que expone los endpoints para la gestión de categorías.
 * <p>
 * Proporciona operaciones CRUD para categorías del catálogo mediante los casos de uso
 * definidos en el dominio, transformando las solicitudes y respuestas entre DTOs y modelos de dominio.
 * </p>
 */
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
    ) {
        this.createCategoryUseCase = createCategoryUseCase;
        this.listCategoriesUseCase = listCategoriesUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
    }

    /**
     * Crea una nueva categoría en el catálogo.
     *
     * @param request DTO con el nombre de la categoría a crear
     * @return respuesta con los datos de la categoría creada
     */
    @PostMapping
    public CategoryResponse create(@RequestBody CreateCategoryRequest request) {
        var category = createCategoryUseCase.create(request.name());
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.isActive()
        );
    }

    /**
     * Obtiene la lista de todas las categorías activas.
     *
     * @return lista de categorías en formato de respuesta
     */
    @GetMapping
    public List<CategoryResponse> list() {
        return listCategoriesUseCase.listAll()
                .stream()
                .map(c -> new CategoryResponse(
                        c.getId(),
                        c.getName(),
                        c.isActive()
                ))
                .toList();
    }

    /**
     * Actualiza el nombre de una categoría existente.
     *
     * @param id identificador de la categoría a actualizar
     * @param request DTO con el nuevo nombre de la categoría
     * @return respuesta con los datos de la categoría actualizada
     */
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

    /**
     * Elimina (desactiva lógicamente) una categoría por su identificador.
     *
     * @param id identificador de la categoría a eliminar
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        deleteCategoryUseCase.deleteById(id);
    }
}