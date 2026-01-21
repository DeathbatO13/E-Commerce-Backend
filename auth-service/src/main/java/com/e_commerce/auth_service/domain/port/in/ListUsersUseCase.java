package com.e_commerce.auth_service.domain.port.in;

import com.e_commerce.auth_service.domain.model.User;

import java.util.List;

public interface ListUsersUseCase {

    List<User> listUsers();

}
