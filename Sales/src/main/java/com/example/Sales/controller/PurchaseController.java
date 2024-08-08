package com.example.Sales.controller;

import com.example.Sales.Dto.PurchaseDTO;
import com.example.Sales.entity.Purchase;
import com.example.Sales.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Purchase> purchaseProduct(@RequestBody PurchaseDTO purchaseDTO) {
        return ResponseEntity.ok(purchaseService.purchaseProduct(purchaseDTO));
    }
}
