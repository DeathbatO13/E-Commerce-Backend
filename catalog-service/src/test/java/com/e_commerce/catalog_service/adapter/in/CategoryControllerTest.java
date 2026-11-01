package com.e_commerce.catalog_service.adapter.in;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.any;
import com.e_commerce.catalog_service.adapter.in.rest.CategoryController;
import com.e_commerce.catalog_service.config.JwtProvider;
import com.e_commerce.catalog_service.config.SecurityConfig;
import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.port.in.CreateCategoryUseCase;
import com.e_commerce.catalog_service.domain.port.in.DeleteCategoryUseCase;
import com.e_commerce.catalog_service.domain.port.in.ListCategoriesUseCase;
import com.e_commerce.catalog_service.domain.port.in.UpdateCategoryUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias para el controlador REST CategoryController.
 * <p>
 * Valida el comportamiento de los endpoints mediante simulaciones de los casos de uso
 * (mockeados) y verificaciones de estado HTTP y cuerpo de respuesta JSON.
 * </p>
 */
@WebMvcTest(CategoryController.class)
@Import(SecurityConfig.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateCategoryUseCase createCategoryUseCase;

    @MockitoBean
    private ListCategoriesUseCase listCategoriesUseCase;

    @MockitoBean
    private UpdateCategoryUseCase updateCategoryUseCase;

    @MockitoBean
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @MockitoBean
    private JwtProvider jwtProvider;

    /**
     * Verifica que al enviar una solicitud POST válida se crea una categoría correctamente
     * y se retorna el estado 200 con los datos esperados en la respuesta JSON.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateCategorySuccessfully() throws Exception {
        Category category = new Category(
                UUID.randomUUID(),
                "Books",
                true
        );

        when(createCategoryUseCase.create("Books"))
                .thenReturn(category);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        { "name": "Books" }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Books"))
                .andExpect(jsonPath("$.active").value(true));
    }

    /**
     * Verifica que al solicitar la lista de categorías se retorna el estado 200
     * y se incluye la categoría esperada en el arreglo JSON de respuesta.
     */
    @Test
    void shouldListCategoriesSuccessfully() throws Exception {
        Category category = new Category(
                UUID.randomUUID(),
                "Books",
                true
        );

        when(listCategoriesUseCase.listAll())
                .thenReturn(List.of(category));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Books"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    /**
     * Verifica que un usuario sin rol ADMIN no puede crear categorías.
     */
    @Test
    @WithMockUser(roles = "CLIENT")
    void shouldRejectCategoryCreationForClient() throws Exception {
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        { "name": "Books" }
                    """))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que crear una categoría con un nombre existente responde 409.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn409WhenCategoryAlreadyExists() throws Exception {
        when(createCategoryUseCase.create("Books"))
                .thenThrow(new IllegalStateException("Category already exists"));

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        { "name": "Books" }
                    """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Category already exists"));
    }

    /**
     * Verifica que un nombre vacío se rechaza con 400.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRejectBlankCategoryName() throws Exception {
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        { "name": "  " }
                    """))
                .andExpect(status().isBadRequest());

        verify(createCategoryUseCase, never()).create(any());
    }

    /**
     * Verifica que DELETE responde 204 No Content.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn204WhenDeletingCategory() throws Exception {
        mockMvc.perform(delete("/categories/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica que no se puede desactivar una categoría con productos activos.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn409WhenCategoryHasActiveProducts() throws Exception {
        UUID categoryId = UUID.randomUUID();
        doThrow(new IllegalStateException("Category has active products"))
                .when(deleteCategoryUseCase).deleteById(categoryId);

        mockMvc.perform(delete("/categories/{id}", categoryId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Category has active products"));
    }
}
