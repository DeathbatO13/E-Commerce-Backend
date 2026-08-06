package com.e_commerce.auth_service.adapter.in.rest;

import com.e_commerce.auth_service.adapter.in.rest.dto.UserResponse;
import com.e_commerce.auth_service.domain.model.Role;
import com.e_commerce.auth_service.domain.model.User;
import com.e_commerce.auth_service.domain.port.in.ChangeUserRoleUseCase;
import com.e_commerce.auth_service.domain.port.in.ListUsersUseCase;
import org.springframework.http.ResponseEntity;
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

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/admin/users/{id}/role")
    public ResponseEntity<Void> changeRole(
            @PathVariable UUID id,
            @RequestParam Role role) {
        if (role == Role.SUPER_ADMIN) {
            throw new IllegalArgumentException("SUPER_ADMIN no puede asignarse");
        }
        changeUserRoleUseCase.changeUserRole(id, role.toString());
        return ResponseEntity.ok().build();
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
