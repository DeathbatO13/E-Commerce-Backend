package com.e_commerce.catalog_service.adapter.in;

import com.e_commerce.catalog_service.adapter.in.rest.ProductController;
import com.e_commerce.catalog_service.application.service.CategoryApplicationService;
import com.e_commerce.catalog_service.config.JwtProvider;
import com.e_commerce.catalog_service.config.SecurityConfig;
import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.model.Product;
import com.e_commerce.catalog_service.domain.port.in.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
@Import(SecurityConfig.class)
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

    @MockitoBean
    private CategoryApplicationService categoryService;

    @MockitoBean
    private JwtProvider jwtProvider;

    /**
     * Verifica que al enviar una solicitud POST válida se crea un producto correctamente
     * y se retorna el estado 200 con los datos esperados en la respuesta JSON.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateProductSuccessfully() throws Exception {
        UUID categoryId = UUID.randomUUID();
        Product product = new Product(
                UUID.randomUUID(),
                "Laptop",
                "Gaming Laptop",
                BigDecimal.valueOf(1600),
                10,
                new Category(categoryId, "Tech", true),
                true
        );

        when(categoryService.existsById(categoryId)).thenReturn(true);
        when(createProductUseCase.create(any(Product.class)))
                .thenReturn(product);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "name": "Laptop",
                            "description": "Gaming Laptop",
                            "price": 1600,
                            "stock": 10,
                            "categoryId": "%s"
                        }
                        """.formatted(categoryId)))
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

    /**
     * Verifica que un precio no positivo se rechaza con 400 antes de llegar al caso de uso.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRejectProductWithInvalidPrice() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "name": "Laptop",
                            "description": "Gaming Laptop",
                            "price": 0,
                            "stock": 10,
                            "categoryId": "%s"
                        }
                        """.formatted(UUID.randomUUID())))
                .andExpect(status().isBadRequest());

        verify(createProductUseCase, never()).create(any(Product.class));
    }
}
