package com.e_commerce.order_service.http;

import com.e_commerce.order_service.adapter.out.http.CartHttpAdapter;
import com.e_commerce.order_service.domain.model.CartSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Prueba el adaptador contra respuestas HTTP simuladas con {@link MockRestServiceServer}
 * (sin levantar el otro servicio): qué request arma y cómo traduce cada status a excepciones
 * del dominio, que GlobalExceptionHandler convierte después en 404 / 409 / 403 / 401 / 502 / 503.
 */
class CartHttpAdapterTest {

    private static final String BASE_URL = "http://cart";
    private static final String TOKEN = "Bearer user-token";

    private MockRestServiceServer server;
    private CartHttpAdapter adapter;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        adapter = new CartHttpAdapter(builder, BASE_URL);
    }

    @Test
    void getActiveCart_forwardsUserTokenAndMapsItems() {
        UUID productId = UUID.randomUUID();
        server.expect(requestTo(BASE_URL + "/cart"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, TOKEN))
                .andRespond(withSuccess("""
                        {"id":"%s","userId":"%s","total":20.00,
                         "items":[{"productId":"%s","quantity":2,"price":10.00}]}
                        """.formatted(UUID.randomUUID(), userId, productId), MediaType.APPLICATION_JSON));

        CartSnapshot cart = adapter.getActiveCart(userId, TOKEN);

        assertEquals(1, cart.items().size());
        assertEquals(productId, cart.items().get(0).productId());
        assertEquals(2, cart.items().get(0).quantity());
        server.verify();
    }

    @Test
    void cartWithoutItems_isEmptySnapshot() {
        server.expect(requestTo(BASE_URL + "/cart"))
                .andRespond(withSuccess("{\"userId\":\"%s\"}".formatted(userId), MediaType.APPLICATION_JSON));

        assertTrue(adapter.getActiveCart(userId, TOKEN).isEmpty());
    }

    @Test
    void cartRejectingTheToken_isUnauthorizedNotBadGateway() {
        server.expect(requestTo(BASE_URL + "/cart"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        // AuthenticationException → 401 en GlobalExceptionHandler (antes salía como 502)
        assertThrows(BadCredentialsException.class, () -> adapter.getActiveCart(userId, TOKEN));
    }

    @Test
    void clearCart_sendsDeleteWithUserToken() {
        server.expect(requestTo(BASE_URL + "/cart"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header(HttpHeaders.AUTHORIZATION, TOKEN))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        adapter.clearCart(userId, TOKEN);

        server.verify();
    }
}
