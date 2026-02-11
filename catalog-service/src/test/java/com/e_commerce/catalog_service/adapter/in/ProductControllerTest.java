package com.e_commerce.catalog_service.adapter.in;

import com.e_commerce.catalog_service.adapter.in.rest.ProductController;
import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.model.Product;
import com.e_commerce.catalog_service.domain.port.in.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias para el controlador REST ProductController.
 * <p>
 * Valida el comportamiento de los endpoints principales mediante mocks de los casos de uso
 * y verificaciones de estado HTTP y contenido de las respuestas JSON.
 * </p>
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListProductUseCase listProductsUseCase;

    @MockitoBean
    private CreateProductUseCase createProductUseCase;

    @MockitoBean
    private GetProductUseCase getProductUseCase;

    @MockitoBean
    private UpdateProductUseCase updateProductUseCase;

    @MockitoBean
    private DeleteProductUseCase deleteProductUseCase;

    /**
     * Verifica que al enviar una solicitud POST válida se crea un producto correctamente
     * y se retorna el estado 200 con los datos esperados en la respuesta JSON.
     */
    @Test
    void shouldCreateProductSuccessfully() throws Exception {
        Product product = new Product(
                UUID.randomUUID(),
                "Laptop",
                "Gaming Laptop",
                BigDecimal.valueOf(1600),
                10,
                new Category(UUID.randomUUID(), "Tech", true),
                true
        );

        when(createProductUseCase.create(any(Product.class)))
                .thenReturn(product);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "name": "Laptop",
                            "description": "Gaming Laptop",
                            "price": 1600,
                            "stock": 10
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1600))
                .andExpect(jsonPath("$.active").value(true));
    }

    /**
     * Verifica que al filtrar productos por nombre mediante parámetro GET
     * se retorna el estado 200 y se incluye el producto esperado en la respuesta JSON.
     */
    @Test
    void shouldListProductsByName() throws Exception {
        Product product = new Product(
                UUID.randomUUID(),
                "Laptop",
                "Gaming laptop",
                BigDecimal.valueOf(2000),
                5,
                new Category(UUID.randomUUID(), "Tech", true),
                true
        );

        when(listProductsUseCase.listByName("Laptop"))
                .thenReturn(List.of(product));

        mockMvc.perform(get("/products")
                        .param("name", "Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }
}