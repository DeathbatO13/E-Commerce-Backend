package com.e_commerce.catalog_service.domain.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad del dominio que representa un producto dentro del catálogo del e-commerce.
 * <p>
 * Contiene la información esencial de un producto: identificación, nombre, descripción,
 * precio, stock disponible, categoría a la que pertenece y su estado de activación.
 * </p>
 */

public class Product {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Category category;
    private boolean active;

    public Product(UUID id, String name, String description, BigDecimal price, Integer stock, Category category, boolean active) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (price == null || price.signum() <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative");
        }
        this.id = Objects.requireNonNull(id);
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = Objects.requireNonNull(category, "Product category is required");
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public Integer getStock() {
        return stock;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isActive() {
        return active;
    }
}
