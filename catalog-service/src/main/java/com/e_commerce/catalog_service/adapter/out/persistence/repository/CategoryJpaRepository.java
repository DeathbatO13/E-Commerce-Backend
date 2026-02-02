package com.e_commerce.catalog_service.adapter.out.persistence.repository;

import com.e_commerce.catalog_service.adapter.out.persistence.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, UUID>{

    Optional<CategoryJpaEntity> findByNameAndActiveTrue(String name);

    List<CategoryJpaEntity> findByActiveTrue();
}
