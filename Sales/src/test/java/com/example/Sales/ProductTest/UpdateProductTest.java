package com.example.Sales.ProductTest;

import com.example.Sales.controller.ProductController;
import com.example.Sales.Dto.ProductDTO;
import com.example.Sales.entity.Product;
import com.example.Sales.exception.GlobalExceptionHandler;
import com.example.Sales.exception.ProductNotFoundException;
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

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class UpdateProductTest {

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
    public void updateProductPositiveTest() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Updated Laptop");
        productDTO.setPrice(new BigDecimal("12000"));
        productDTO.setQuantity(8);

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Laptop");
        updatedProduct.setPrice(new BigDecimal("12000"));
        updatedProduct.setQuantity(8);

        when(productService.updateProduct(anyLong(), any(ProductDTO.class))).thenReturn(updatedProduct);

        String jsonRequest = om.writeValueAsString(productDTO);

        MvcResult result = mockMvc.perform(put("/api/products/1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        System.out.println(resultContent);
        Product responseProduct = om.readValue(resultContent, Product.class);

        assertEquals("Updated Laptop", responseProduct.getName());
        assertEquals(new BigDecimal("12000"), responseProduct.getPrice());
        assertEquals(8, responseProduct.getQuantity());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void updateProductNegativeTest_ProductNotFound() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Non-existent Product");
        productDTO.setPrice(new BigDecimal("5000"));
        productDTO.setQuantity(5);

        when(productService.updateProduct(anyLong(), any(ProductDTO.class)))
                .thenThrow(new ProductNotFoundException("Product not found with ID: 1"));

        String jsonRequest = om.writeValueAsString(productDTO);

        MvcResult result = mockMvc.perform(put("/api/products/1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        System.out.println(resultContent);
        assertEquals("Product not found with ID: 1", resultContent);
    }
}
