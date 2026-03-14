package com.e_commerce.order_service.adapter;

import com.e_commerce.order_service.domain.ports.in.CreateOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.GetOrderUseCase;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.e_commerce.order_service.adapter.in.rest.OrderController;


@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateOrderUseCase createOrderUseCase;

    @MockitoBean
    private GetOrderUseCase getOrdersUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOrder_shouldReturn201() throws Exception {

        UUID orderId = UUID.randomUUID();

        when(createOrderUseCase.createOrder(
                any(),
                any(),
                any(),
                any()
        )).thenReturn(orderId);

        String requestBody = """
                {
                  "address": "Street 123",
                  "total": 20,
                  "items": [
                    {
                      "productId": "%s",
                      "quantity": 2,
                      "price": 10
                    }
                  ]
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(
                        post("/orders")
                                .header("X-User-Id", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated());
    }

}
