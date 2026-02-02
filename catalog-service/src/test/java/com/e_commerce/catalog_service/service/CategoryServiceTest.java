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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryApplicationService service;

    @Test
    void shouldCreateCategorySuccessfully(){

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

    @Test
    void shouldFailIfCategoryExists(){

        when(categoryRepository.findByName("Electronics"))
                .thenReturn(Optional.of(new Category()));


        assertThrows(
                IllegalArgumentException.class, () -> service.create("Electronics")
        );
    }

}
