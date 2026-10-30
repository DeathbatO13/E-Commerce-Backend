package com.e_commerce.order_service.adapter;

import com.e_commerce.order_service.adapter.in.rest.OrderController;
import com.e_commerce.order_service.config.InternalServiceAuthFilter;
import com.e_commerce.order_service.config.JwtProvider;
import com.e_commerce.order_service.config.SecurityConfig;
import com.e_commerce.order_service.domain.model.Order;
import com.e_commerce.order_service.domain.model.OrderItem;
import com.e_commerce.order_service.domain.ports.in.CancelOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.CreateOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.GetOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.PayOrderUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import({SecurityConfig.class, InternalServiceAuthFilter.class})
@TestPropertySource(properties = "security.internal-token=" + OrderControllerTest.INTERNAL_TOKEN)
public class OrderControllerTest {

    static final String INTERNAL_TOKEN = "test-internal-token";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateOrderUseCase createOrderUseCase;

    @MockitoBean
    private GetOrderUseCase getOrdersUseCase;

    @MockitoBean
    private PayOrderUseCase payOrderUseCase;

    @MockitoBean
    private CancelOrderUseCase cancelOrderUseCase;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void createOrder_shouldReturn201() throws Exception {

        UUID orderId = UUID.randomUUID();

        when(createOrderUseCase.createOrder(
                any(),
                any(),
                any()
        )).thenReturn(orderId);

        String requestBody = """
                {
                  "address": "Street 123"
                }
                """;

        mockMvc.perform(
                        post("/orders")
                                .with(csrf())
                                .header("Authorization", "Bearer token123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated());
    }

    @Test
    void payOrder_withInternalToken_shouldReturn200() throws Exception {
        UUID orderId = UUID.randomUUID();

        mockMvc.perform(post("/orders/{id}/pay", orderId)
                        .header(InternalServiceAuthFilter.HEADER, INTERNAL_TOKEN))
                .andExpect(status().isOk());

        verify(payOrderUseCase).payOrder(orderId);
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000", roles = "CLIENT")
    void payOrder_asClient_shouldReturn403() throws Exception {
        mockMvc.perform(post("/orders/{id}/pay", UUID.randomUUID()))
                .andExpect(status().isForbidden());

        verify(payOrderUseCase, never()).payOrder(any());
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000", roles = "ADMIN")
    void payOrder_asAdmin_shouldReturn403() throws Exception {
        mockMvc.perform(post("/orders/{id}/pay", UUID.randomUUID()))
                .andExpect(status().isForbidden());

        verify(payOrderUseCase, never()).payOrder(any());
    }

    @Test
    void payOrder_withWrongInternalToken_shouldBeRejected() throws Exception {
        mockMvc.perform(post("/orders/{id}/pay", UUID.randomUUID())
                        .header(InternalServiceAuthFilter.HEADER, "wrong-token"))
                .andExpect(status().isForbidden());

        verify(payOrderUseCase, never()).payOrder(any());
    }

    // --- Respuestas de error (GlobalExceptionHandler) ---

    private static final String CLIENT_ID = "123e4567-e89b-12d3-a456-426614174000";

    @Test
    @WithMockUser(username = CLIENT_ID, roles = "CLIENT")
    void getOrder_notFound_shouldReturn404WithBody() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(getOrdersUseCase.getOrderById(orderId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Order not found: " + orderId));
    }

    @Test
    @WithMockUser(username = CLIENT_ID, roles = "CLIENT")
    void getOrder_ofAnotherUser_shouldReturn403WithBody() throws Exception {
        UUID orderId = UUID.randomUUID();
        Order foreignOrder = new Order(UUID.randomUUID(), "Street 1",
                List.of(new OrderItem(UUID.randomUUID(), 1, BigDecimal.TEN)), BigDecimal.TEN);
        when(getOrdersUseCase.getOrderById(orderId)).thenReturn(Optional.of(foreignOrder));

        mockMvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @WithMockUser(username = CLIENT_ID, roles = "CLIENT")
    void cancelOrder_ofAnotherUser_shouldReturn403AndNotCancel() throws Exception {
        UUID orderId = UUID.randomUUID();
        Order foreignOrder = new Order(UUID.randomUUID(), "Street 1",
                List.of(new OrderItem(UUID.randomUUID(), 1, BigDecimal.TEN)), BigDecimal.TEN);
        when(getOrdersUseCase.getOrderById(orderId)).thenReturn(Optional.of(foreignOrder));

        mockMvc.perform(post("/orders/{id}/cancel", orderId))
                .andExpect(status().isForbidden());

        verify(cancelOrderUseCase, never()).cancelOrder(any());
    }

    @Test
    void payOrder_alreadyPaid_shouldReturn409() throws Exception {
        UUID orderId = UUID.randomUUID();
        doThrow(new IllegalStateException("Only CREATED orders can be paid"))
                .when(payOrderUseCase).payOrder(orderId);

        mockMvc.perform(post("/orders/{id}/pay", orderId)
                        .header(InternalServiceAuthFilter.HEADER, INTERNAL_TOKEN))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Only CREATED orders can be paid"));
    }

    @Test
    @WithMockUser(username = CLIENT_ID, roles = "CLIENT")
    void getOrder_withInvalidUuid_shouldReturn400() throws Exception {
        mockMvc.perform(get("/orders/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(username = CLIENT_ID, roles = "CLIENT")
    void createOrder_withMalformedJson_shouldReturn400() throws Exception {
        mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer token123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"address\": "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(createOrderUseCase, never()).createOrder(any(), any(), any());
    }

}
