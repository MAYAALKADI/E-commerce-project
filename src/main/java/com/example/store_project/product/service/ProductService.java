package com.example.store_project.product.service;

import com.example.store_project.inventory.client.InventoryClient;
import com.example.store_project.product.model.Product;
import com.example.store_project.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryClient inventoryClient;

    public ProductService(ProductRepository productRepository, InventoryClient inventoryClient) {
        this.productRepository = productRepository;
        this.inventoryClient = inventoryClient;
    }
@Transactional(readOnly = true)
    public Page<Product> getAllProducts(String category, Pageable pageable) {
        if (category != null && !category.trim().isEmpty()) {
            return productRepository.findByCategory(category, pageable);
        }
        return productRepository.findAll(pageable);
    }

    public Product addProduct(Product product) {
        int qty = product.getQuantity() == null ? 0 : product.getQuantity();
        Product saved = productRepository.save(product);

        // تسجيل الكمية بالمستودع. إذا فشل، منمسح المنتج حتى ما يضل منتج بلا مخزون
        try {
            inventoryClient.registerProduct(saved.getId(), qty);
        } catch (RuntimeException e) {
            productRepository.delete(saved);
            throw e;
        }

        saved.setQuantity(qty);
        return saved;
    }

@Transactional(readOnly = true)    public Optional<Product> getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        product.ifPresent(p -> p.setQuantity(inventoryClient.getAvailable(p.getId())));
        return product;
    }
}