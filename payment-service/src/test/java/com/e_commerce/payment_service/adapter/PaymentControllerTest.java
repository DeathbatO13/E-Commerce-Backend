package com.e_commerce.payment_service.adapter;

import com.e_commerce.payment_service.adapter.in.rest.PaymentController;
import com.e_commerce.payment_service.config.JwtProvider;
import com.e_commerce.payment_service.config.SecurityConfig;
import com.e_commerce.payment_service.domain.model.Payment;
import com.e_commerce.payment_service.domain.model.PaymentStatus;
import com.e_commerce.payment_service.domain.port.in.ProcessPaymentUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@Import(SecurityConfig.class)
class PaymentControllerTest {

    private static final String TOKEN = "Bearer user-token";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcessPaymentUseCase processPaymentUseCase;

    @MockitoBean
    private JwtProvider jwtProvider;

    private static String body(UUID orderId, String amount, String simulateResult) {
        String simulate = simulateResult != null ? ", \"simulateResult\": \"" + simulateResult + "\"" : "";
        return "{ \"orderId\": \"" + orderId + "\", \"amount\": " + amount + simulate + " }";
    }

    @Test
    @WithMockUser
    void processPayment_returnsPaymentAndForwardsTheUserToken() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(processPaymentUseCase.processPayment(eq(orderId), any(), eq(PaymentStatus.REJECTED), eq(TOKEN)))
                .thenReturn(new Payment(orderId, new BigDecimal("150.00"), PaymentStatus.REJECTED, "TX-1"));

        mockMvc.perform(post("/payments/process")
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(orderId, "150.00", "REJECTED")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.transactionId").value("TX-1"));
    }

    @Test
    @WithMockUser
    void invalidSimulateResult_returns400() throws Exception {
        mockMvc.perform(post("/payments/process")
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UUID.randomUUID(), "150.00", "MAYBE")))
                .andExpect(status().isBadRequest());

        verify(processPaymentUseCase, never()).processPayment(any(), any(), any(), any());
    }

    @Test
    @WithMockUser
    void amountMismatch_returns400() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(processPaymentUseCase.processPayment(eq(orderId), any(), any(), eq(TOKEN)))
                .thenThrow(new IllegalArgumentException("Amount 1.00 does not match order total 150.00"));

        mockMvc.perform(post("/payments/process")
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(orderId, "1.00", null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Amount 1.00 does not match order total 150.00"));
    }

    @Test
    @WithMockUser
    void orderOfAnotherUser_returns403() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(processPaymentUseCase.processPayment(eq(orderId), any(), any(), eq(TOKEN)))
                .thenThrow(new AccessDeniedException("Order belongs to another user"));

        mockMvc.perform(post("/payments/process")
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(orderId, "150.00", null)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void orderAlreadyPaid_returns409() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(processPaymentUseCase.processPayment(eq(orderId), any(), any(), eq(TOKEN)))
                .thenThrow(new IllegalStateException("Order is not pending payment (status: PAID)"));

        mockMvc.perform(post("/payments/process")
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(orderId, "150.00", null)))
                .andExpect(status().isConflict());
    }

    @Test
    void withoutAuthentication_isRejected() throws Exception {
        mockMvc.perform(post("/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UUID.randomUUID(), "150.00", null)))
                .andExpect(status().isForbidden());

        verify(processPaymentUseCase, never()).processPayment(any(), any(), any(), any());
    }
}
