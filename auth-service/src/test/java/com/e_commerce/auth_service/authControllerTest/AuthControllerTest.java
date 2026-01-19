package com.e_commerce.auth_service.authControllerTest;

import com.e_commerce.auth_service.adapter.in.rest.AuthController;
import com.e_commerce.auth_service.config.JwtProvider;
import com.e_commerce.auth_service.domain.model.Role;
import com.e_commerce.auth_service.domain.model.User;
import com.e_commerce.auth_service.domain.port.in.LoginUseCase;
import com.e_commerce.auth_service.domain.port.in.RegisterUserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Solo para Spring Boot 3.4+
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; // CORREGIDO
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva seguridad para el test unitario
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // O @MockBean si usas una versión anterior a Spring Boot 3.4
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private LoginUseCase loginUseCase;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Test
    void debeRegistrarUsuario() throws Exception {
        User user = new User(
                UUID.randomUUID(),
                "test@mail.com",
                "hashed",
                "Administrator",
                Set.of(Role.CLIENT),
                true
        );

        when(registerUserUseCase.register(anyString(), anyString(), anyString()))
                .thenReturn(user);

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "email": "test@mail.com",
                              "password": "123456",
                              "fullname":"Administrator"
                            }
                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@mail.com")); // CAST ELIMINADO
    }

    @Test
    void debeIniciarUsuario() throws Exception {
        when(loginUseCase.login(anyString(), anyString()))
                .thenReturn("fake-token");

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "email": "test@mail.com",
                              "password": "123456"
                            }
                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-token")); // CAST ELIMINADO
    }
}