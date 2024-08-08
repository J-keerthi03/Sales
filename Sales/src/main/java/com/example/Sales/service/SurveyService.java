package com.example.Sales.service;

import com.example.Sales.entity.Product;
import com.example.Sales.entity.Purchase;
import com.example.Sales.entity.Survey;
import com.example.Sales.repository.ProductRepository;
import com.example.Sales.repository.PurchaseRepository;
import com.example.Sales.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    public List<Survey> generateSurveyReport() {
        try {
            List<Product> products = productRepository.findAll();
            return products.stream().map(this::calculateProfitAndLoss).collect(Collectors.toList());
        } catch (Exception e) {
            // Handle exceptions, log the error and rethrow a runtime exception
            e.printStackTrace();
            throw new RuntimeException("Failed to generate survey report: " + e.getMessage());
        }
    }

    private Survey calculateProfitAndLoss(Product product) {
        try {
            BigDecimal totalPurchases = purchaseRepository.findAll().stream()
                    .filter(purchase -> purchase.getProduct().equals(product))
                    .map(Purchase::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal costPrice = product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity()));
            BigDecimal profit = totalPurchases.subtract(costPrice);
            BigDecimal loss = (profit.compareTo(BigDecimal.ZERO) < 0) ? profit.negate() : BigDecimal.ZERO;

            Survey survey = new Survey();
            survey.setProduct(product);
            survey.setProfit(profit);
            survey.setLoss(loss);

            return surveyRepository.save(survey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to calculate profit and loss for product ID "
                    + product.getId() + ": " + e.getMessage());
        }
    }
}
