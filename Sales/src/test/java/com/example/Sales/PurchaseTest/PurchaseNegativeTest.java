package com.example.Sales.PurchaseTest;

import com.example.Sales.Dto.PurchaseDTO;
import com.example.Sales.config.LocalDateTimeSerializer;
import com.example.Sales.controller.PurchaseController;
import com.example.Sales.entity.Product;
import com.example.Sales.entity.Purchase;
import com.example.Sales.exception.GlobalExceptionHandler;
import com.example.Sales.service.PurchaseService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PurchaseNegativeTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Mock
    private PurchaseService purchaseService;

    @InjectMocks
    private PurchaseController purchaseController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configure ObjectMapper
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        this.om = objectMapper;

        mockMvc = MockMvcBuilders
                .standaloneSetup(purchaseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void purchaseProductPositiveTest() throws Exception {
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

        MvcResult result = mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(purchaseDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        String expectedContent = om.writeValueAsString(purchase);

        System.out.println("Expected Content: " + expectedContent);
        System.out.println("Actual Content: " + resultContent);

        assertEquals(expectedContent, resultContent);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void purchaseProductNegativeTest_ProductNotFound() throws Exception {
        PurchaseDTO purchaseDTO = new PurchaseDTO();
        purchaseDTO.setProductId(1L);
        purchaseDTO.setQuantity(2);

        doThrow(new RuntimeException("Product not found")).when(purchaseService).purchaseProduct(any(PurchaseDTO.class));

        MvcResult result = mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(purchaseDTO)))
                .andExpect(status().isNotFound())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        assertEquals("{\"status\":\"ERROR\",\"message\":\"Product not found\"}", resultContent);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void purchaseProductNegativeTest_NotEnoughStock() throws Exception {
        PurchaseDTO purchaseDTO = new PurchaseDTO();
        purchaseDTO.setProductId(1L);
        purchaseDTO.setQuantity(2);

        doThrow(new RuntimeException("Not enough stock available")).when(purchaseService).purchaseProduct(any(PurchaseDTO.class));

        MvcResult result = mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(purchaseDTO)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        assertEquals("{\"status\":\"ERROR\",\"message\":\"Not enough stock available\"}", resultContent);
    }
}
