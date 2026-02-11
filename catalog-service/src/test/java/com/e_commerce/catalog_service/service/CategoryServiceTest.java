package com.e_commerce.catalog_service.service;

import com.e_commerce.catalog_service.application.service.CategoryApplicationService;
import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.port.out.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para CategoryApplicationService.
 * <p>
 * Valida la lógica de negocio de creación, verificación de duplicados y desactivación
 * de categorías, utilizando mocks del repositorio.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryApplicationService service;

    /**
     * Verifica que se crea correctamente una categoría cuando no existe previamente
     * con el mismo nombre, y que se persiste con estado activo.
     */
    @Test
    void shouldCreateCategorySuccessfully() {
        when(categoryRepository.findByName("Electronics"))
                .thenReturn(Optional.empty());

        when(categoryRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        Category category = service.create("Electronics");

        assertNotNull(category);
        assertEquals("Electronics", category.getName());
        assertTrue(category.isActive());

        verify(categoryRepository).save(any());
    }

    /**
     * Verifica que se lanza IllegalArgumentException al intentar crear una categoría
     * cuyo nombre ya existe en el sistema.
     */
    @Test
    void shouldFailIfCategoryExists() {
        when(categoryRepository.findByName("Electronics"))
                .thenReturn(Optional.of(new Category()));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.create("Electronics")
        );
    }

    /**
     * Verifica que al solicitar la desactivación de una categoría se delega
     * correctamente la llamada al método deactivate del repositorio.
     */
    @Test
    void shouldDeactivateCategory() {
        UUID id = UUID.randomUUID();

        service.deleteById(id);

        verify(categoryRepository).deactivate(id);
    }
}