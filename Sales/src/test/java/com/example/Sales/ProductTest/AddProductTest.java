package com.example.Sales.ProductTest;

import com.example.Sales.Dto.ProductDTO;
import com.example.Sales.Dto.ResponseDto;
import com.example.Sales.controller.ProductController;
import com.example.Sales.entity.Product;
import com.example.Sales.exception.GlobalExceptionHandler;
import com.example.Sales.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AddProductTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

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
    public void addProductPositiveTest() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Laptop");
        productDTO.setPrice(new BigDecimal("10000"));
        productDTO.setQuantity(10);

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Laptop");
        mockProduct.setPrice(new BigDecimal("10000"));
        mockProduct.setQuantity(10);

        ResponseDto responseDTO = new ResponseDto();
        responseDTO.setStatus("SUCCESS");
        responseDTO.setMessage("Product added successfully");

        when(productService.addProduct(productDTO)).thenReturn(mockProduct);

        String jsonRequest = om.writeValueAsString(productDTO);

        MvcResult result = mockMvc.perform(post("/api/products")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        System.out.println(resultContent);
        ResponseDto actualResponseDto = om.readValue(resultContent, ResponseDto.class);

        Assert.assertEquals("SUCCESS", actualResponseDto.getStatus());
        Assert.assertEquals("Product added successfully", actualResponseDto.getMessage());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void addProductNegativeTest() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Laptop");
        productDTO.setPrice(new BigDecimal("10000"));
        productDTO.setQuantity(10);

        String jsonRequest = om.writeValueAsString(productDTO);

        when(productService.addProduct(Mockito.any(ProductDTO.class)))
                .thenThrow(new RuntimeException("Failed to add product: Service error"));

        MvcResult result = mockMvc.perform(post("/api/products")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        System.out.println(resultContent);
        ResponseDto responseDto = om.readValue(resultContent, ResponseDto.class);

        Assert.assertEquals("ERROR", responseDto.getStatus());
        Assert.assertEquals("Failed to add product: Service error", responseDto.getMessage());
    }
}
