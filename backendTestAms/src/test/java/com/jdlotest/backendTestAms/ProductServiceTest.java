package com.jdlotest.backendTestAms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.jdlotest.backendTestAms.exception.ResourceNotFoundException;
import com.jdlotest.backendTestAms.model.ProductDetail;
import com.jdlotest.backendTestAms.service.ProductService;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductServiceTest {
	@Autowired
    private MockMvc mockMvc;

	@MockBean
    private ProductService productService;
    
	@Test
    @DisplayName("Debe orquestar las llamadas al Mock y devolver el Set de productos")
    void shouldFetchSimilarProductsSuccessfully() throws Exception {
        String productId = "1";
        
        ProductDetail product1 = new ProductDetail();
        product1.setId("2");
        product1.setName("Zapatillas");
        product1.setPrice(new java.math.BigDecimal("99.99"));
        
        ProductDetail product2 = new ProductDetail();
        product2.setId("3");
        product2.setName("Pastel");
        product2.setPrice(new java.math.BigDecimal("15.50"));
        
        when(productService.getSimilarProducts(productId)).thenReturn(Set.of(product1, product2));

        Set<ProductDetail> result = productService.getSimilarProducts(productId);

        assertNotNull(result);
        assertEquals(2, result.size());
        
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Zapatillas")));
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Pastel")));
    }
	
	@Test
    @DisplayName("Debe orquestar las llamadas al Mock y devolver el Set de productos vacio")
    void shouldFetchSimilarProductsNonSuccessfully() throws Exception {
        String productId = "1";
        
        when(productService.getSimilarProducts(productId)).thenReturn(Set.of());

        Set<ProductDetail> result = productService.getSimilarProducts(productId);

        assertNotNull(result);
        assertEquals(0, result.size());
    }
	
	@Test
    @DisplayName("Debe orquestar las llamadas al Mock y devolver la excepcion")
    void shouldNonFetchSimilarProducts() throws Exception {
        String productId = "1";
        
        when(productService.getSimilarProducts(productId))
            .thenThrow(new ResourceNotFoundException("Product with ID " + productId + " not found"));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.getSimilarProducts(productId);
        });

        assertEquals("Product with ID " + productId + " not found", exception.getMessage());
    }

}