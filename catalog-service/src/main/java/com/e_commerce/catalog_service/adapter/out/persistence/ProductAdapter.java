package com.e_commerce.catalog_service.adapter.out.persistence;

import com.e_commerce.catalog_service.adapter.out.persistence.entity.CategoryJpaEntity;
import com.e_commerce.catalog_service.adapter.out.persistence.entity.ProductJpaEntity;
import com.e_commerce.catalog_service.adapter.out.persistence.mapper.ProductMapper;
import com.e_commerce.catalog_service.adapter.out.persistence.repository.CategoryJpaRepository;
import com.e_commerce.catalog_service.adapter.out.persistence.repository.ProductJpaRepository;
import com.e_commerce.catalog_service.domain.model.Product;
import com.e_commerce.catalog_service.domain.port.out.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de salida que implementa el puerto ProductRepository utilizando JPA.
 * <p>
 * Traduce entre el modelo de dominio (Product) y las entidades JPA,
 * y delega las operaciones de persistencia al repositorio Spring Data JPA.
 * </p>
 */
@Component
public class ProductAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;

    public ProductAdapter(ProductJpaRepository productJpaRepository,
                          CategoryJpaRepository categoryJpaRepository) {
        this.productJpaRepository = productJpaRepository;
        this.categoryJpaRepository = categoryJpaRepository;
    }

    /**
     * Guarda o actualiza un producto en la base de datos.
     *
     * @param product el producto del dominio a persistir
     * @return el producto persistido mapeado al modelo de dominio
     * @throws IllegalArgumentException si la categoría asociada no existe
     */
    @Override
    public Product save(Product product) {
        CategoryJpaEntity categoryEntity = categoryJpaRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + product.getCategory().getId()));

        ProductJpaEntity entity = ProductMapper.toEntity(product);
        entity.setCategory(categoryEntity);

        return ProductMapper.toDomain(productJpaRepository.save(entity));
    }

    /**
     * Busca un producto por su ID, retornando solo productos activos.
     *
     * @param id identificador del producto
     * @return Optional con el producto del dominio si existe y está activo
     */
    @Override
    public Optional<Product> findById(UUID id) {
        return productJpaRepository.findById(id)
                .filter(ProductJpaEntity::isActive)
                .map(ProductMapper::toDomain);
    }

    /**
     * Obtiene todos los productos activos.
     *
     * @return lista de productos activos en el modelo de dominio
     */
    @Override
    public List<Product> findAll() {
        return productJpaRepository.findByActiveTrue()
                .stream()
                .map(ProductMapper::toDomain)
                .toList();
    }

    /**
     * Busca productos activos cuyo nombre contenga el texto indicado (insensible a mayúsculas).
     *
     * @param name parte o texto completo del nombre a buscar
     * @return lista de productos que coinciden
     */
    @Override
    public List<Product> findByName(String name) {
        return productJpaRepository
                .findByNameContainingIgnoreCaseAndActiveTrue(name)
                .stream()
                .map(ProductMapper::toDomain)
                .toList();
    }

    /**
     * Obtiene todos los productos activos que pertenecen a una categoría específica.
     *
     * @param categoryId identificador de la categoría
     * @return lista de productos de la categoría
     */
    @Override
    public List<Product> findByCategory(UUID categoryId) {
        return productJpaRepository
                .findByCategoryIdAndActiveTrue(categoryId)
                .stream()
                .map(ProductMapper::toDomain)
                .toList();
    }

    /**
     * Desactiva lógicamente un producto marcándolo como no activo.
     *
     * @param id identificador del producto a desactivar
     * @throws IllegalArgumentException si el producto no existe
     */
    @Override
    public void deactivate(UUID id) {
        ProductJpaEntity entity = productJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        entity.setActive(false);
    }

    /**
     * Verifica si existen productos activos asociados a una categoría.
     *
     * @param categoryId identificador de la categoría
     * @return true si hay al menos un producto activo en la categoría
     */
    @Override
    public boolean existsByCategory(UUID categoryId) {
        return productJpaRepository.existsByCategoryIdAndActiveTrue(categoryId);
    }
}