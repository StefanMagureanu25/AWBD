package com.stefan.ecommerce.services;

import com.stefan.ecommerce.entities.Product;
import com.stefan.ecommerce.repositories.ProductRepository;
import com.stefan.ecommerce.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllActiveProducts() {
        Product p1 = new Product("A", "desc", BigDecimal.TEN, 10);
        Product p2 = new Product("B", "desc", BigDecimal.ONE, 5);
        when(productRepository.findByActiveTrue()).thenReturn(Arrays.asList(p1, p2));
        List<Product> products = productService.findAllActiveProducts();
        assertEquals(2, products.size());
    }

    @Test
    void testFindById() {
        Product p = new Product("A", "desc", BigDecimal.TEN, 10);
        p.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        Optional<Product> found = productService.findById(1L);
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
    }

    @Test
    void testSaveProduct() {
        Product p = new Product("A", "desc", BigDecimal.TEN, 10);
        when(productRepository.save(p)).thenReturn(p);
        Product saved = productRepository.save(p);
        assertNotNull(saved);
    }
} 