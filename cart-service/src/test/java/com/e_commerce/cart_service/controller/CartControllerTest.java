package com.e_commerce.cart_service.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import java.util.List;
import io.jsonwebtoken.Claims;
import com.e_commerce.cart_service.adapter.in.rest.CartController;
import com.e_commerce.cart_service.config.JwtProvider;
import com.e_commerce.cart_service.config.SecurityConfig;
import com.e_commerce.cart_service.domain.model.Cart;
import com.e_commerce.cart_service.domain.port.in.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@Import(SecurityConfig.class)
public class CartControllerTest {

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

    /** El controller toma el userId de Authentication.getName() (el subject del JWT). */
    private static RequestPostProcessor client(UUID userId) {
        return user(userId.toString()).roles("CLIENT");
    }

    @Test
    void shouldReturnCart() throws Exception {

        UUID userId = UUID.randomUUID();

        Cart cart = new Cart(userId);
        cart.addProduct(UUID.randomUUID(), BigDecimal.valueOf(100), 2);

        when(getCartUseCase.getCart(userId)).thenReturn(cart);

        mockMvc.perform(get("/cart").with(client(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.total").value(200));
    }

    @Test
    void shouldAddProductToCart() throws Exception {

        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Cart cart = new Cart(userId);
        cart.addProduct(productId, BigDecimal.valueOf(50), 2);

        when(addProductCartUseCase.addProduct(userId, productId, 2))
                .thenReturn(cart);

        String body = """
        {
          "productId": "%s",
          "quantity": 2
        }
        """.formatted(productId);

        mockMvc.perform(post("/cart/items")
                        .with(client(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].productId").value(productId.toString()));

        verify(addProductCartUseCase).addProduct(userId, productId, 2);
    }

    @Test
    void shouldRejectAddItemWithInvalidQuantity() throws Exception {

        UUID userId = UUID.randomUUID();

        mockMvc.perform(post("/cart/items")
                        .with(client(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        { "productId": "%s", "quantity": 0 }
                        """.formatted(UUID.randomUUID())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));

        verify(addProductCartUseCase, never()).addProduct(any(), any(), anyInt());
    }

    @Test
    void shouldRejectAddItemWithoutProductId() throws Exception {

        mockMvc.perform(post("/cart/items")
                        .with(client(UUID.randomUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        { "quantity": 1 }
                        """))
                .andExpect(status().isBadRequest());

        verify(addProductCartUseCase, never()).addProduct(any(), any(), anyInt());
    }

    @Test
    void shouldUpdateItemQuantity() throws Exception {

        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Cart cart = new Cart(userId);
        cart.addProduct(productId, BigDecimal.valueOf(50), 3);

        when(updateCartItemQuantityUseCase.updateQuantity(userId, productId, 3))
                .thenReturn(cart);

        mockMvc.perform(put("/cart/items/{productId}", productId)
                        .with(client(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        { "quantity": 3 }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(150));

        verify(updateCartItemQuantityUseCase).updateQuantity(userId, productId, 3);
    }

    @Test
    void shouldRejectRequestWithoutAuthentication() throws Exception {

        mockMvc.perform(get("/cart"))
                .andExpect(status().isUnauthorized());

        verify(getCartUseCase, never()).getCart(any());
    }

    @Test
    void invalidToken_returns401() throws Exception {
        when(jwtProvider.validateToken(anyString())).thenReturn(false);

        mockMvc.perform(get("/cart").header("Authorization", "Bearer expired"))
                .andExpect(status().isUnauthorized());

        verify(getCartUseCase, never()).getCart(any());
    }

    @Test
    void clientToken_reachesOwnCart() throws Exception {
        UUID userId = UUID.randomUUID();
        givenToken("client-token", userId.toString(), "CLIENT");
        when(getCartUseCase.getCart(userId)).thenReturn(new Cart(userId));

        mockMvc.perform(get("/cart").header("Authorization", "Bearer client-token"))
                .andExpect(status().isOk());

        verify(getCartUseCase).getCart(userId);
    }

    @Test
    void tokenWithoutCartRole_returns403() throws Exception {
        // /cart/** es para CLIENT y ADMIN; SUPER_ADMIN solo tiene /cart/admin/**
        givenToken("super-token", UUID.randomUUID().toString(), "SUPER_ADMIN");

        mockMvc.perform(get("/cart").header("Authorization", "Bearer super-token"))
                .andExpect(status().isForbidden());

        verify(getCartUseCase, never()).getCart(any());
    }

    /** Hace que {@code token} sea válido para el JwtAuthFilter real, con ese subject y esos roles. */
    private void givenToken(String token, String subject, String... roles) {
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(subject);
        when(claims.get("roles", List.class)).thenReturn(List.of(roles));
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getClaims(token)).thenReturn(claims);
    }
}
