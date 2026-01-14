package com.e_commerce.auth_service.domain.port.out;

import com.e_commerce.auth_service.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);

    User save(User user);
}
