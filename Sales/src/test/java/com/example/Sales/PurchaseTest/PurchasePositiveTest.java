package com.example.Sales.PurchaseTest;

import com.example.Sales.Dto.PurchaseDTO;
import com.example.Sales.entity.Product;
import com.example.Sales.entity.Purchase;
import com.example.Sales.service.PurchaseService;
import com.example.Sales.controller.PurchaseController;
import com.example.Sales.config.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PurchasePositiveTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private PurchaseService purchaseService;

    @InjectMocks
    private PurchaseController purchaseController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configure ObjectMapper to use custom LocalDateTimeSerializer
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);

        mockMvc = MockMvcBuilders
                .standaloneSetup(purchaseController)
                .build();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void purchaseProductPositiveTest() throws Exception {
        // Prepare test data
        PurchaseDTO purchaseDTO = new PurchaseDTO();
        purchaseDTO.setProductId(1L);
        purchaseDTO.setQuantity(2);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(10);
        product.setPrice(BigDecimal.valueOf(100));

        Purchase purchase = new Purchase();
        purchase.setProduct(product);
        purchase.setQuantity(2);
        purchase.setTotalAmount(BigDecimal.valueOf(200));
        purchase.setPurchaseDate(LocalDateTime.now());


        when(purchaseService.purchaseProduct(any(PurchaseDTO.class))).thenReturn(purchase);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purchaseDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        // Convert the `purchase` to a JSON string
        String expectedContent = objectMapper.writeValueAsString(purchase);

        System.out.println("Expected: " + expectedContent);
        System.out.println("Actual: " + resultContent);

        assertEquals(expectedContent, resultContent);
    }
}
