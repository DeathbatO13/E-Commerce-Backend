package com.e_commerce.catalog_service.application.service;

import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.port.in.CreateCategoryUseCase;
import com.e_commerce.catalog_service.domain.port.in.DeleteCategoryUseCase;
import com.e_commerce.catalog_service.domain.port.in.ListCategoriesUseCase;
import com.e_commerce.catalog_service.domain.port.in.UpdateCategoryUseCase;
import com.e_commerce.catalog_service.domain.port.out.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de aplicación que implementa los casos de uso relacionados con la gestión de categorías.
 * <p>
 * Coordina la lógica de negocio para la creación, consulta, actualización y desactivación
 * de categorías, actuando como intermediario entre los puertos de entrada y el repositorio.
 * </p>
 */
@Service
@Transactional
public class CategoryApplicationService implements CreateCategoryUseCase,
        ListCategoriesUseCase, UpdateCategoryUseCase, DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public CategoryApplicationService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Crea una nueva categoría verificando que no exista ya una con el mismo nombre.
     *
     * @param name nombre de la categoría a crear
     * @return la categoría creada y persistida
     * @throws IllegalArgumentException si ya existe una categoría con el mismo nombre
     */
    @Override
    public Category create(String name) {
        if (categoryRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Category already exists");
        }
        return categoryRepository.save(
                new Category(UUID.randomUUID(), name, true));
    }

    /**
     * Desactiva (eliminación lógica) una categoría por su identificador.
     *
     * @param categoryId identificador de la categoría a desactivar
     */
    @Override
    public void deleteById(UUID categoryId) {
        categoryRepository.deactivate(categoryId);
    }

    /**
     * Obtiene la lista de todas las categorías activas.
     *
     * @return lista completa de categorías
     */
    @Override
    public List<Category> listAll() {
        return categoryRepository.findAll();
    }

    /**
     * Actualiza el nombre de una categoría existente.
     *
     * @param uuid identificador de la categoría a actualizar
     * @param name nuevo nombre de la categoría
     * @return la categoría actualizada y persistida
     * @throws IllegalArgumentException si la categoría no existe
     */
    @Override
    public Category update(UUID uuid, String name) {
        Category category = categoryRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        category.setName(name);
        return categoryRepository.save(category);
    }
}