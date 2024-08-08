package com.example.Sales.service;

import com.example.Sales.Dto.ProductDTO;
import com.example.Sales.entity.Product;
import com.example.Sales.exception.ProductNotFoundException;
import com.example.Sales.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product addProduct(ProductDTO productDTO) {
        try {
            Product product = new Product();
            product.setName(productDTO.getName());
            product.setPrice(productDTO.getPrice());
            product.setQuantity(productDTO.getQuantity());
            return productRepository.save(product);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add product: " + e.getMessage());
        }
    }

    public Product getProductById(Long id) {
        try {
            Optional<Product> productOpt = productRepository.findById(id);
            if (productOpt.isPresent()) {
                return productOpt.get();
            } else {
                throw new RuntimeException("Product not found with ID: " + id);
            }
        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException("Failed to get product by ID: " + e.getMessage());
        }
    }

    public List<Product> getAllProducts() {
        try {
            return productRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get all products: " + e.getMessage());
        }
    }

    public Product updateProduct(Long id, ProductDTO productDTO) {
        try {
            Optional<Product> productOpt = productRepository.findById(id);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                product.setName(productDTO.getName());
                product.setPrice(productDTO.getPrice());
                product.setQuantity(productDTO.getQuantity());
                return productRepository.save(product);
            } else {
                throw new ProductNotFoundException("Product not found with ID: " + id);
            }
        } catch (ProductNotFoundException e) {
            throw e; // Rethrow the specific exception for not found product
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update product: " + e.getMessage());
        }
    }

    public void deleteProduct(Long id) {
        try {
            if (productRepository.existsById(id)) {
                productRepository.deleteById(id);
            } else {
                throw new ProductNotFoundException("Product not found with ID: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete product: " + e.getMessage());
        }
    }
}

