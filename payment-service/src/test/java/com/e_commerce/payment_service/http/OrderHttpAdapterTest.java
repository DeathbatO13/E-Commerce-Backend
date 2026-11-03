package com.e_commerce.payment_service.http;

import com.e_commerce.payment_service.adapter.out.http.OrderHttpAdapter;
import com.e_commerce.payment_service.domain.exception.ResourceNotFoundException;
import com.e_commerce.payment_service.domain.model.OrderSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Prueba el adaptador contra respuestas HTTP simuladas con {@link MockRestServiceServer}
 * (sin levantar el otro servicio): qué request arma y cómo traduce cada status a excepciones
 * del dominio, que GlobalExceptionHandler convierte después en 404 / 409 / 403 / 401 / 502 / 503.
 */
class OrderHttpAdapterTest {

    private static final String BASE_URL = "http://order";
    private static final String USER_TOKEN = "Bearer user-token";
    private static final String INTERNAL_TOKEN = "internal-secret";

    private MockRestServiceServer server;
    private OrderHttpAdapter adapter;
    private final UUID orderId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        adapter = new OrderHttpAdapter(builder, BASE_URL, INTERNAL_TOKEN);
    }

    @Test
    void getOrder_forwardsUserTokenAndMapsSummary() {
        UUID userId = UUID.randomUUID();
        server.expect(requestTo(BASE_URL + "/orders/" + orderId))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, USER_TOKEN))
                .andRespond(withSuccess("""
                        {"id":"%s","userId":"%s","status":"CREATED","total":150.00,"address":"Street 1"}
                        """.formatted(orderId, userId), MediaType.APPLICATION_JSON));

        OrderSummary order = adapter.getOrder(orderId, USER_TOKEN);

        assertEquals(userId, order.userId());
        assertTrue(order.isPendingPayment());
        assertEquals(0, new BigDecimal("150.00").compareTo(order.total()));
        server.verify();
    }

    @Test
    void getOrder_translates404And403() {
        server.expect(requestTo(BASE_URL + "/orders/" + orderId)).andRespond(withStatus(HttpStatus.NOT_FOUND));
        server.expect(requestTo(BASE_URL + "/orders/" + orderId)).andRespond(withStatus(HttpStatus.FORBIDDEN));

        assertThrows(ResourceNotFoundException.class, () -> adapter.getOrder(orderId, USER_TOKEN));
        assertThrows(AccessDeniedException.class, () -> adapter.getOrder(orderId, USER_TOKEN));
    }

    @Test
    void markOrderAsPaid_usesInternalTokenNotTheUserToken() {
        server.expect(requestTo(BASE_URL + "/orders/" + orderId + "/pay"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Internal-Token", INTERNAL_TOKEN))
                .andExpect(headerDoesNotExist(HttpHeaders.AUTHORIZATION))
                .andRespond(withStatus(HttpStatus.OK));

        adapter.markOrderAsPaid(orderId);

        server.verify();
    }

    @Test
    void markOrderAsPaid_translates404And409() {
        server.expect(requestTo(BASE_URL + "/orders/" + orderId + "/pay")).andRespond(withStatus(HttpStatus.NOT_FOUND));
        server.expect(requestTo(BASE_URL + "/orders/" + orderId + "/pay")).andRespond(withStatus(HttpStatus.CONFLICT));

        assertThrows(ResourceNotFoundException.class, () -> adapter.markOrderAsPaid(orderId));
        assertThrows(IllegalStateException.class, () -> adapter.markOrderAsPaid(orderId));
    }
}
