package com.example.Sales.ProductTest;

import com.example.Sales.controller.ProductController;
import com.example.Sales.exception.GlobalExceptionHandler;
import com.example.Sales.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DeleteProductTest {

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
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteProductPositiveTest() throws Exception {
        mockMvc.perform(delete("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(productService).deleteProduct(1L);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteProductNegativeTest_ProductNotFound() throws Exception {
        doThrow(new RuntimeException("Product not found with ID: 1")).when(productService).deleteProduct(anyLong());

        MvcResult result = mockMvc.perform(delete("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        System.out.println(resultContent);
        assertEquals("{\"status\":\"ERROR\",\"message\":\"Product not found with ID: 1\"}", resultContent);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteProductNegativeTest_FailedDeletion() throws Exception {
        doThrow(new RuntimeException("Failed to delete product")).when(productService).deleteProduct(anyLong());

        MvcResult result = mockMvc.perform(delete("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        System.out.println(resultContent);
        assertEquals("{\"status\":\"ERROR\",\"message\":\"Failed to delete product\"}", resultContent);
    }
}
