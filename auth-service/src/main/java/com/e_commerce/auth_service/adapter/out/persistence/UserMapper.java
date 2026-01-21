package com.e_commerce.auth_service.adapter.out.persistence;

import com.e_commerce.auth_service.adapter.in.rest.dto.UserResponse;
import com.e_commerce.auth_service.domain.model.User;
import com.e_commerce.auth_service.domain.model.Role;

import java.util.stream.Collectors;

/**
 * Clase de mapeo bidireccional entre el modelo de dominio y la entidad JPA.
 * <p>
 * Transforma:
 * <ul>
 *   <li>{@link UserJpaEntity} → {@link User} (al leer de BD)</li>
 *   <li>{@link User} → {@link UserJpaEntity} (al guardar/actualizar en BD)</li>
 * </ul>
 * </p>
 * <p>
 * Maneja la conversión de roles (String → Enum y viceversa) de forma segura.
 * </p>
 */
class UserMapper{

    /**
     * Mapea entidad persistente a modelo de dominio.
     * Convierte los roles almacenados como String a enum {@link Role}.
     */
    static User toDomain(UserJpaEntity entity){
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getFullname(),
                entity.getRoles().stream()
                        .map(Role::valueOf)
                        .collect(Collectors.toSet()),
                entity.isEnabled()
        );
    }

    /**
     * Mapea modelo de dominio a entidad persistente.
     * Convierte los enums {@link Role} a sus nombres como String.
     */
    static UserJpaEntity toEntity(User user){
        return UserJpaEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .fullname(user.getFullname())
                .roles(user.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()))
                .enabled(user.isEnabled())
                .build();
    }
}
