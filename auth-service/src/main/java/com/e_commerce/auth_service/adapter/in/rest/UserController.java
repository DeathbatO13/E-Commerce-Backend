package com.e_commerce.auth_service.adapter.in.rest;

import com.e_commerce.auth_service.adapter.in.rest.dto.UserResponse;
import com.e_commerce.auth_service.domain.model.User;
import com.e_commerce.auth_service.domain.port.in.ChangeUserRoleUseCase;
import com.e_commerce.auth_service.domain.port.in.ListUsersUseCase;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequestMapping("/admin")
public class UserController{

    private final ListUsersUseCase listUsersUseCase;
    private final ChangeUserRoleUseCase changeUserRoleUseCase;

    public UserController(ListUsersUseCase listUsersUseCase, ChangeUserRoleUseCase changeUserRoleUseCase) {
        this.changeUserRoleUseCase = changeUserRoleUseCase;
        this.listUsersUseCase = listUsersUseCase;
    }

    @GetMapping("/users")
    public List<UserResponse> listUsers() {
        return listUsersUseCase.listUsers()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PutMapping("/users/{id}/role")
    public void changeRole(
            @PathVariable UUID id,
            @RequestParam String role
    ) {
        changeUserRoleUseCase.changeUserRole(id, role);
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
