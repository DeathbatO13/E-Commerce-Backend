package com.e_commerce.catalog_service.adapter.out.persistence;

import com.e_commerce.catalog_service.adapter.out.persistence.entity.CategoryJpaEntity;
import com.e_commerce.catalog_service.adapter.out.persistence.mapper.CategoryMapper;
import com.e_commerce.catalog_service.adapter.out.persistence.repository.CategoryJpaRepository;
import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.port.out.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de salida que implementa el puerto CategoryRepository utilizando JPA.
 * <p>
 * Traduce entre el modelo de dominio (Category) y las entidades JPA,
 * y delega las operaciones de persistencia al repositorio Spring Data JPA.
 * </p>
 */
@Component
public class CategoryAdapter implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    public CategoryAdapter(CategoryJpaRepository categoryJpaRepository) {
        this.categoryJpaRepository = categoryJpaRepository;
    }

    /**
     * Guarda o actualiza una categoría en la base de datos.
     *
     * @param category la categoría del dominio a persistir
     * @return la categoría persistida mapeada al modelo de dominio
     */
    @Override
    public Category save(Category category) {
        CategoryJpaEntity entity = CategoryMapper.toEntity(category);
        return CategoryMapper.toDomain(categoryJpaRepository.save(entity));
    }

    /**
     * Obtiene todas las categorías activas.
     *
     * @return lista de categorías activas en el modelo de dominio
     */
    @Override
    public List<Category> findAll() {
        return categoryJpaRepository.findByActiveTrue()
                .stream()
                .map(CategoryMapper::toDomain)
                .toList();
    }

    /**
     * Busca una categoría por su ID, retornando solo categorías activas.
     *
     * @param id identificador de la categoría
     * @return Optional con la categoría del dominio si existe y está activa
     */
    @Override
    public Optional<Category> findById(UUID id) {
        return categoryJpaRepository.findById(id)
                .filter(CategoryJpaEntity::isActive)
                .map(CategoryMapper::toDomain);
    }

    /**
     * Busca una categoría activa por su nombre exacto.
     *
     * @param name nombre exacto de la categoría
     * @return Optional con la categoría del dominio si existe y está activa
     */
    @Override
    public Optional<Category> findByName(String name) {
        return categoryJpaRepository.findByNameAndActiveTrue(name)
                .map(CategoryMapper::toDomain);
    }

    /**
     * Desactiva lógicamente una categoría marcándola como no activa.
     *
     * @param id identificador de la categoría a desactivar
     * @throws IllegalArgumentException si la categoría no existe
     */
    @Override
    public void deactivate(UUID id) {
        CategoryJpaEntity entity = categoryJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        entity.setActive(false);
    }
}