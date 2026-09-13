package com.e_commerce.order_service.adapter;

import com.e_commerce.order_service.adapter.in.rest.OrderController;
import com.e_commerce.order_service.domain.ports.in.CancelOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.CreateOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.GetOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.PayOrderUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

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

}
