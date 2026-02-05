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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
public class ProductControllerTest {


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

/*
    @Test
    void shouldCreateProductSuccessfully() throws Exception{

        Product product = new Product(
                UUID.randomUUID(),
                "Laptop",
                "Gaming Laptop",
                BigDecimal.valueOf(1600),
                10,
                new Category(UUID.randomUUID(), "Tech", true),
                true
        );

        when(createProductUseCase.create(product))
                .thenReturn(product);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        [
                        {"name": "Laptop"}
                        ]
                        """))
    }*/


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
