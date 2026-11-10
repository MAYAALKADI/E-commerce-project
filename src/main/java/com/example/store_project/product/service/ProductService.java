package com.example.store_project.product.service;

import com.example.store_project.inventory.client.InventoryClient;
import com.example.store_project.product.model.Product;
import com.example.store_project.product.model.ProductPage;
import com.example.store_project.product.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryClient inventoryClient;
    private final ProductCacheService productCacheService;

    public ProductService(ProductRepository productRepository,
                          InventoryClient inventoryClient,
                          ProductCacheService productCacheService) {
        this.productRepository = productRepository;
        this.inventoryClient = inventoryClient;
        this.productCacheService = productCacheService;
    }

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(String category, Pageable pageable) {
        if (category != null && !category.trim().isEmpty()) {
            return productRepository.findByCategory(category, pageable);
        }
        return productRepository.findAll(pageable);
    }

    @Cacheable(cacheNames = "productList",
            key = "(#category == null ? 'all' : #category) + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public ProductPage getProductPage(String category, Pageable pageable) {
        Page<Product> p = getAllProducts(category, pageable);
        return new ProductPage(p.getContent(), p.getNumber(), p.getSize(),
                p.getTotalElements(), p.getTotalPages());
    }

    @Transactional
    @CacheEvict(cacheNames = "productList", allEntries = true)
    public Product addProduct(Product product) {
        int qty = product.getQuantity() == null ? 0 : product.getQuantity();
        Product saved = productRepository.save(product);

        try {
            inventoryClient.registerProduct(saved.getId(), qty);
        } catch (RuntimeException e) {
            productRepository.delete(saved);
            throw e;
        }

        saved.setQuantity(qty);
        return saved;
    }

    public Optional<Product> getProductById(Long id) {
        Product cached = productCacheService.findById(id);
        if (cached == null) {
            return Optional.empty();
        }
        Product copy = new Product(cached.getId(), cached.getName(), cached.getPrice(),
                cached.getDescription(), cached.getCategory(), cached.getImageUrl(), null);
        copy.setQuantity(inventoryClient.getAvailable(id));
        return Optional.of(copy);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "productList", allEntries = true),
            @CacheEvict(cacheNames = "productDetail", key = "#id")
    })
    public Product updateProduct(Long id, Product updated) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        p.setName(updated.getName());
        p.setPrice(updated.getPrice());
        p.setDescription(updated.getDescription());
        p.setCategory(updated.getCategory());
        p.setImageUrl(updated.getImageUrl());
        return productRepository.save(p);
    }
}