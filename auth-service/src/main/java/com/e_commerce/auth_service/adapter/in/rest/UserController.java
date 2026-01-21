package com.e_commerce.auth_service.adapter.in.rest;

import com.e_commerce.auth_service.adapter.in.rest.dto.UserResponse;
import com.e_commerce.auth_service.domain.model.User;
import com.e_commerce.auth_service.domain.port.in.ListUsersUseCase;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;



@RestController
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequestMapping("/admin")
public class UserController{

    private final ListUsersUseCase listUsersUseCase;

    public UserController(ListUsersUseCase listUsersUseCase) {
        this.listUsersUseCase = listUsersUseCase;
    }

    @GetMapping("/users")
    public List<UserResponse> listUsers() {
        return listUsersUseCase.listUsers()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId().toString(),
                user.getEmail(),
                user.getRoles()
                        .stream()
                        .map(Enum::name)
                        .toList(),
                user.isEnabled()
        );
    }

}
