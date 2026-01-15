package com.e_commerce.auth_service.adapter.out.persistence;

import com.e_commerce.auth_service.domain.model.User;
import com.e_commerce.auth_service.domain.model.Role;


import java.util.stream.Collectors;

class UserMapper{

    static User toDomain(UserJpaEntity entity){
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRoles().stream()
                        .map(Role::valueOf)
                        .collect(Collectors.toSet()),
                entity.isEnabled()
        );
    }

    static UserJpaEntity toEntity(User user){
        return UserJpaEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()))
                .enabled(user.isEnabled())
                .build();
    }
}
