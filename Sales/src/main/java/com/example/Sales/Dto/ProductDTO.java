package com.example.Sales.Dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductDTO {
    private Long id;

    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "Price cannot be null")
    private BigDecimal price;

    @NotNull(message = "Quantity cannot be null")
    private Integer quantity;
}
