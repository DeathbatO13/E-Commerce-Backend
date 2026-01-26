package com.e_commerce.catalog_service.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryJpaEntity{

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<ProductJpaEntity> products = new ArrayList<>();

}
