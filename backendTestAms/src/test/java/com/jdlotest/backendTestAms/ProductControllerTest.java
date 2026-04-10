package com.jdlotest.backendTestAms;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.jdlotest.backendTestAms.exception.ResourceNotFoundException;
import com.jdlotest.backendTestAms.model.ProductDetail;
import com.jdlotest.backendTestAms.service.ProductService;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {
	@Autowired
    private MockMvc mockMvc; // Simula peticiones HTTP sin levantar el servidor real

    @MockBean
    private ProductService productService; // Creamos un "doble" de tu servicio
    
    @Test
    @DisplayName("Debe retornar 200 OK y la lista de productos similares")
    void shouldReturnSimilarProducts() throws Exception {
        String productId = "1";
        ProductDetail similarProduct = new ProductDetail();
        similarProduct.setId("2");
        similarProduct.setName("Zapatillas");
        similarProduct.setPrice(new BigDecimal(99.99));
        
        given(productService.getSimilarProducts(productId)).willReturn(Set.of(similarProduct));

        mockMvc.perform(get("/product/{productId}/similar", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("2"))
                .andExpect(jsonPath("$[0].name").value("Zapatillas"))
                .andExpect(jsonPath("$[0].price").value(new BigDecimal(99.99)));
    }
    
    @Test
    @DisplayName("Debe retornar 200 OK y la lista de productos similares mayor a uno")
    void shouldReturnSimilarProductsList() throws Exception {
        String productId = "1";
        ProductDetail similarProduct = new ProductDetail();
        similarProduct.setId("2");
        similarProduct.setName("Zapatillas");
        similarProduct.setPrice(new BigDecimal(99.99));
        ProductDetail similarProduct2 = new ProductDetail();
        similarProduct2.setId("3");
        similarProduct2.setName("Pastel");
        similarProduct2.setPrice(new BigDecimal(99.99));
        ProductDetail similarProduct3 = new ProductDetail();
        similarProduct3.setId("4");
        similarProduct3.setName("Celular");
        similarProduct3.setPrice(new BigDecimal(99.99));
        
        given(productService.getSimilarProducts(productId)).willReturn(Set.of(similarProduct, similarProduct2, similarProduct3));

        mockMvc.perform(get("/product/{productId}/similar", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[?(@.id == '2')].name").value("Zapatillas"))
                .andExpect(jsonPath("$[?(@.id == '3')].name").value("Pastel"))
                .andExpect(jsonPath("$[?(@.id == '4')].name").value("Celular"));
    }
    
    @Test
    @DisplayName("Debe retornar 200 OK y la lista de productos similares vacia")
    void shouldReturnEmpty() throws Exception {
        String productId = "1";
        ProductDetail similarProduct = new ProductDetail();
        similarProduct.setId("2");
        similarProduct.setName("Zapatillas");
        similarProduct.setPrice(new BigDecimal(99.99));
        
        given(productService.getSimilarProducts(productId)).willReturn(Set.of());

        mockMvc.perform(get("/product/{productId}/similar", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }
    
    @Test
    @DisplayName("Debe retornar 200 OK y la lista de productos similares vacia")
    void shouldReturnErrorNotFound() throws Exception {
        String productId = "1";
        
        given(productService.getSimilarProducts(productId)).willThrow(new ResourceNotFoundException("Product with ID " + productId + " not found"));

        mockMvc.perform(get("/product/{productId}/similar", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
