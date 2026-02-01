package com.e_commerce.catalog_service.domain.model;

import lombok.Setter;

import java.util.UUID;

public class Category{

    private UUID id;
    @Setter
    private String name;
    private boolean active;

    public Category(){}

    public Category(UUID id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }


}
