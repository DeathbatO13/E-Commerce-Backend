package com.e_commerce.cart_service.http;

import com.e_commerce.cart_service.adapter.out.http.CatalogHttpAdapter;
import com.e_commerce.cart_service.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Prueba el adaptador contra respuestas HTTP simuladas con {@link MockRestServiceServer}
 * (sin levantar el otro servicio): qué request arma y cómo traduce cada status a excepciones
 * del dominio, que GlobalExceptionHandler convierte después en 404 / 409 / 403 / 401 / 502 / 503.
 */
class CatalogHttpAdapterTest {

    private static final String BASE_URL = "http://catalog";

    private MockRestServiceServer server;
    private CatalogHttpAdapter adapter;
    private final UUID productId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        adapter = new CatalogHttpAdapter(builder, BASE_URL);
    }

    private String product(boolean active) {
        return """
                {"id":"%s","name":"TV","price":199.90,"active":%s,"stock":3}
                """.formatted(productId, active);
    }

    @Test
    void activeProduct_returnsItsPrice() {
        server.expect(requestTo(BASE_URL + "/products/" + productId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(product(true), MediaType.APPLICATION_JSON));

        assertEquals(0, new BigDecimal("199.90").compareTo(adapter.getPrice(productId)));
        server.verify();
    }

    @Test
    void inactiveProduct_isNotFound() {
        server.expect(requestTo(BASE_URL + "/products/" + productId))
                .andRespond(withSuccess(product(false), MediaType.APPLICATION_JSON));

        assertThrows(ResourceNotFoundException.class, () -> adapter.getPrice(productId));
    }

    @Test
    void catalog404_isNotFound() {
        server.expect(requestTo(BASE_URL + "/products/" + productId))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(ResourceNotFoundException.class, () -> adapter.getPrice(productId));
    }

    @Test
    void catalog500_propagatesAsDownstreamError() {
        server.expect(requestTo(BASE_URL + "/products/" + productId))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        // GlobalExceptionHandler responde 502
        assertThrows(RestClientResponseException.class, () -> adapter.getPrice(productId));
    }

    @Test
    void catalogDown_propagatesAsResourceAccess() {
        server.expect(requestTo(BASE_URL + "/products/" + productId))
                .andRespond(request -> { throw new IOException("Connection refused"); });

        // GlobalExceptionHandler responde 503
        assertThrows(ResourceAccessException.class, () -> adapter.getPrice(productId));
    }
}
