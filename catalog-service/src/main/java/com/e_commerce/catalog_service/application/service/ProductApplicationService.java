package com.e_commerce.catalog_service.application.service;

import com.e_commerce.catalog_service.domain.model.Product;
import com.e_commerce.catalog_service.domain.port.in.*;
import com.e_commerce.catalog_service.domain.port.out.CategoryRepository;
import com.e_commerce.catalog_service.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de aplicación que implementa los casos de uso relacionados con la gestión de productos.
 * <p>
 * Actúa como orquestador entre los puertos de entrada (casos de uso) y los puertos de salida (repositorios),
 * coordinando la lógica de negocio del catálogo de productos.
 * </p>
 */
@Service
@Transactional
public class ProductApplicationService implements
        CreateProductUseCase, ListProductUseCase, SearchProductUseCase,
        DeleteProductUseCase, UpdateProductUseCase, GetProductUseCase {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductApplicationService(ProductRepository productRepository,
                                     CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Crea un nuevo producto validando que la categoría asociada exista.
     *
     * @param product datos del producto a crear
     * @return el producto creado y persistido
     * @throws IllegalArgumentException si la categoría no existe
     */
    @Override
    public Product create(Product product) {
        if (!categoryRepository.findById(product.getCategory().getId()).isPresent()) {
            throw new IllegalArgumentException("Category not found");
        }
        return productRepository.save(product);
    }

    /**
     * Lista todos los productos activos del catálogo.
     *
     * @return lista de todos los productos
     */
    @Override
    public List<Product> listAll() {
        return productRepository.findAll();
    }

    /**
     * Lista los productos pertenecientes a una categoría específica.
     *
     * @param categoryId identificador de la categoría
     * @return lista de productos de la categoría
     */
    @Override
    public List<Product> listByCategory(UUID categoryId) {
        return productRepository.findByCategory(categoryId);
    }

    /**
     * Lista los productos cuyo nombre coincida parcial o totalmente con el criterio.
     *
     * @param name texto de búsqueda en el nombre
     * @return lista de productos que coinciden
     */
    @Override
    public List<Product> listByName(String name) {
        return productRepository.findByName(name);
    }

    /**
     * Busca productos por nombre (implementación delegada al repositorio).
     *
     * @param name texto o parte del nombre a buscar
     * @return lista de productos que coinciden con el criterio
     */
    @Override
    public List<Product> searchByName(String name) {
        return productRepository.findByName(name);
    }

    /**
     * Desactiva (eliminación lógica) un producto por su identificador.
     *
     * @param productId identificador del producto a desactivar
     */
    @Override
    public void deleteById(UUID productId) {
        productRepository.deactivate(productId);
    }

    /**
     * Actualiza los datos de un producto existente.
     *
     * @param product producto con los datos actualizados
     * @return el producto actualizado y persistido
     */
    @Override
    public Product update(Product product) {
        return productRepository.save(product);
    }

    /**
     * Obtiene un producto por su identificador único.
     *
     * @param uuid identificador del producto
     * @return el producto encontrado
     * @throws IllegalArgumentException si el producto no existe
     */
    @Override
    public Product getById(UUID uuid) {
        return productRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }
}