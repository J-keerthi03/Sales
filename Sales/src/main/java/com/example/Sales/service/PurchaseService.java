package com.example.Sales.service;

import com.example.Sales.Dto.PurchaseDTO;
import com.example.Sales.entity.Product;
import com.example.Sales.entity.Purchase;
import com.example.Sales.repository.ProductRepository;
import com.example.Sales.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PurchaseService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    public Purchase purchaseProduct(PurchaseDTO purchaseDTO) {
        try {
            Optional<Product> optionalProduct = productRepository.findById(purchaseDTO.getProductId());
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                if (product.getQuantity() >= purchaseDTO.getQuantity()) {
                    BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(purchaseDTO.getQuantity()));

                    product.setQuantity(product.getQuantity() - purchaseDTO.getQuantity());
                    productRepository.save(product);

                    Purchase purchase = new Purchase();
                    purchase.setProduct(product);
                    purchase.setQuantity(purchaseDTO.getQuantity());
                    purchase.setTotalAmount(totalAmount);
                    purchase.setPurchaseDate(java.time.LocalDateTime.now());

                    return purchaseRepository.save(purchase);
                } else {
                    throw new RuntimeException("Not enough stock available");
                }
            } else {
                throw new RuntimeException("Product not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to complete the purchase: " + e.getMessage());
        }
    }
}
