package com.e_commerce.catalog_service.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Product {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Category category;
    private boolean active;

    public Product(UUID id, String name, String description, BigDecimal price, Integer stock, Category category, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
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
