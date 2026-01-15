package com.e_commerce.auth_service.authControllerTest;

import com.e_commerce.auth_service.adapter.in.rest.AuthController;
import com.e_commerce.auth_service.domain.model.Role;
import com.e_commerce.auth_service.domain.model.User;
import com.e_commerce.auth_service.domain.port.in.LoginUseCase;
import com.e_commerce.auth_service.domain.port.in.RegisterUserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthController.class)
public class AuthControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private LoginUseCase loginUseCase;

    @Test
    void shouldRegisterUser() throws Exception {
        User user = new User(
                UUID.randomUUID(),
                "test@mail.com",
                "hashed",
                Set.of(Role.CLIENT),
                true
        );

        when(registerUserUseCase.register(anyString(), anyString()))
                .thenReturn(user);

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "email": "test@mail.com",
                              "password": "123456"
                            }
                        """)
                )
                .andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.email").value("test@mail.com"));
    }

    @Test
    void shouldLoginUser() throws Exception {
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
                .andExpect((ResultMatcher) jsonPath("$.token").value("fake-token"));
    }
}
