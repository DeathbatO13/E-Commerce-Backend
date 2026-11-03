package com.e_commerce.order_service.http;

import com.e_commerce.order_service.adapter.out.http.CatalogHttpAdapter;
import com.e_commerce.order_service.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void activeProduct_returnsCatalogPrice() {
        server.expect(requestTo(BASE_URL + "/products/" + productId))
                .andRespond(withSuccess("""
                        {"id":"%s","name":"TV","price":15.00,"active":true}
                        """.formatted(productId), MediaType.APPLICATION_JSON));

        assertEquals(0, new BigDecimal("15.00").compareTo(adapter.getProductPrice(productId)));
        server.verify();
    }

    @Test
    void inactiveOrMissingProduct_isNotFound() {
        server.expect(requestTo(BASE_URL + "/products/" + productId))
                .andRespond(withSuccess("""
                        {"id":"%s","name":"TV","price":15.00,"active":false}
                        """.formatted(productId), MediaType.APPLICATION_JSON));
        server.expect(requestTo(BASE_URL + "/products/" + productId))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(ResourceNotFoundException.class, () -> adapter.getProductPrice(productId));
        assertThrows(ResourceNotFoundException.class, () -> adapter.getProductPrice(productId));
    }

    @Test
    void catalogDown_propagatesAsResourceAccess() {
        server.expect(requestTo(BASE_URL + "/products/" + productId))
                .andRespond(request -> { throw new IOException("Connection refused"); });

        assertThrows(ResourceAccessException.class, () -> adapter.getProductPrice(productId));
    }
}
