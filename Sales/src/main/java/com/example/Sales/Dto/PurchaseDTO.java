package com.example.Sales.Dto;


import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class PurchaseDTO {
    private Long productId;
    private int quantity;
    private BigDecimal totalAmount;
}
