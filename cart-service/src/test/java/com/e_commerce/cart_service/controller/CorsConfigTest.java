package com.e_commerce.cart_service.controller;

import com.e_commerce.cart_service.adapter.in.rest.CartController;
import com.e_commerce.cart_service.config.CorsConfig;
import com.e_commerce.cart_service.config.JwtProvider;
import com.e_commerce.cart_service.config.SecurityConfig;
import com.e_commerce.cart_service.domain.port.in.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * El preflight (OPTIONS) llega sin JWT: SecurityConfig debe responderlo con las cabeceras CORS
 * para los orígenes configurados y rechazarlo para el resto.
 */
@WebMvcTest(CartController.class)
@Import({SecurityConfig.class, CorsConfig.class})
@TestPropertySource(properties = "cors.allowed-origins=http://localhost:5173, https://shop.example.com")
class CorsConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetCartUseCase getCartUseCase;

    @MockitoBean
    private AddProductCartUseCase addProductCartUseCase;

    @MockitoBean
    private UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase;

    @MockitoBean
    private ClearCartUseCase clearCartUseCase;

    @MockitoBean
    private RemoveProductFromCartUseCase removeProductFromCartUseCase;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Test
    void preflightFromAllowedOrigin_isAccepted() throws Exception {
        mockMvc.perform(options("/cart/items")
                        .header(HttpHeaders.ORIGIN, "https://shop.example.com")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Authorization, Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://shop.example.com"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    void preflightFromUnknownOrigin_isRejected() throws Exception {
        mockMvc.perform(options("/cart/items")
                        .header(HttpHeaders.ORIGIN, "https://evil.example.com")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }
}
