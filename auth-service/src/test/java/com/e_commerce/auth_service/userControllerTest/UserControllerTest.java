package com.e_commerce.auth_service.userControllerTest;

import com.e_commerce.auth_service.adapter.in.rest.UserController;
import com.e_commerce.auth_service.config.JwtProvider;
import com.e_commerce.auth_service.config.SecurityConfig;
import com.e_commerce.auth_service.domain.model.Role;
import com.e_commerce.auth_service.domain.model.User;
import com.e_commerce.auth_service.domain.port.in.ChangeUserRoleUseCase;
import com.e_commerce.auth_service.domain.port.in.ListUsersUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de autorización de {@link UserController} con la {@link SecurityConfig} real:
 * ADMIN y SUPER_ADMIN listan usuarios, solo SUPER_ADMIN cambia roles.
 */
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListUsersUseCase listUsersUseCase;

    @MockitoBean
    private ChangeUserRoleUseCase changeUserRoleUseCase;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPuedeListarUsuarios() throws Exception {
        when(listUsersUseCase.listUsers()).thenReturn(List.of(
                new User(UUID.randomUUID(), "c@mail.com", "hash", "Cliente", Set.of(Role.CLIENT), true)));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("c@mail.com"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void clienteNoPuedeListarUsuarios() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());

        verify(listUsersUseCase, never()).listUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminNoPuedeCambiarRoles() throws Exception {
        mockMvc.perform(post("/admin/users/{id}/role", UUID.randomUUID()).param("role", "ADMIN"))
                .andExpect(status().isForbidden());

        verify(changeUserRoleUseCase, never()).changeUserRole(any(), any());
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void superAdminPuedeCambiarRoles() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(post("/admin/users/{id}/role", userId).param("role", "ADMIN"))
                .andExpect(status().isOk());

        verify(changeUserRoleUseCase).changeUserRole(userId, "ADMIN");
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void cambiarElRolDelSuperAdminResponde409() throws Exception {
        UUID superAdminId = UUID.randomUUID();
        doThrow(new IllegalStateException("SUPER_ADMIN role cannot be changed"))
                .when(changeUserRoleUseCase).changeUserRole(superAdminId, "CLIENT");

        mockMvc.perform(post("/admin/users/{id}/role", superAdminId).param("role", "CLIENT"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("SUPER_ADMIN role cannot be changed"));
    }
}
