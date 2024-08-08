package com.example.Sales.ProductTest;

import com.example.Sales.controller.ProductController;
import com.example.Sales.entity.Product;
import com.example.Sales.exception.GlobalExceptionHandler;
import com.example.Sales.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class GetProductTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Include the global exception handler
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getAllProductsPositiveTest_Admin() throws Exception {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(new BigDecimal("10000"));
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Smartphone");
        product2.setPrice(new BigDecimal("5000"));
        product2.setQuantity(20);

        List<Product> products = Arrays.asList(product1, product2);

        when(productService.getAllProducts()).thenReturn(products);

        MvcResult result = mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        List<Product> actualProducts = Arrays.asList(om.readValue(resultContent, Product[].class));

        assertEquals(2, actualProducts.size());
        assertEquals("Laptop", actualProducts.get(0).getName());
        assertEquals("Smartphone", actualProducts.get(1).getName());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void getAllProductsPositiveTest_User() throws Exception {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(new BigDecimal("10000"));
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Smartphone");
        product2.setPrice(new BigDecimal("5000"));
        product2.setQuantity(20);

        List<Product> products = Arrays.asList(product1, product2);

        when(productService.getAllProducts()).thenReturn(products);

        MvcResult result = mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        List<Product> actualProducts = Arrays.asList(om.readValue(resultContent, Product[].class));

        assertEquals(2, actualProducts.size());
        assertEquals("Laptop", actualProducts.get(0).getName());
        assertEquals("Smartphone", actualProducts.get(1).getName());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getAllProductsNegativeTest_Admin_Exception() throws Exception {
        when(productService.getAllProducts()).thenThrow(new RuntimeException("Database error"));

        MvcResult result = mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        assertEquals("{\"status\":\"ERROR\",\"message\":\"Database error\"}", resultContent);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void getAllProductsNegativeTest_User_Exception() throws Exception {
        when(productService.getAllProducts()).thenThrow(new RuntimeException("Database error"));

        MvcResult result = mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        System.out.println(resultContent);
        assertEquals("{\"status\":\"ERROR\",\"message\":\"Database error\"}", resultContent);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getAllProductsPositiveTest_Admin_EmptyList() throws Exception {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        MvcResult result = mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        System.out.println(resultContent);
        List<Product> actualProducts = Arrays.asList(om.readValue(resultContent, Product[].class));

        assertEquals(0, actualProducts.size());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void getAllProductsPositiveTest_User_EmptyList() throws Exception {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        MvcResult result = mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        System.out.println(resultContent);
        List<Product> actualProducts = Arrays.asList(om.readValue(resultContent, Product[].class));

        assertEquals(0, actualProducts.size());
    }
}
