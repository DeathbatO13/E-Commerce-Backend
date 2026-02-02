package com.e_commerce.catalog_service.adapter.out.persistence.repository;

import com.e_commerce.catalog_service.adapter.out.persistence.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, UUID>{

    List<ProductJpaEntity> findByActiveTrue();

    List<ProductJpaEntity> findByCategoryIdAndActiveTrue(UUID categoryId);

    List<ProductJpaEntity> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    boolean existsByCategoryIdAndActiveTrue(UUID categoryId);

}
