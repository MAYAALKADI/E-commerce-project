package com.example.store_project.product.service;

import com.example.store_project.product.model.Product;
import com.example.store_project.product.repository.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ProductCacheService {

    private final ProductRepository productRepository;

    public ProductCacheService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // بدون readOnly عن قصد: القراءة هون من الـ Primary، عشان ما نعبّي الكاش بنسخة قديمة من الـ Replica
    // (null ما بينخزّن لأنو CacheConfig فيه disableCachingNullValues)
    @Cacheable(cacheNames = "productDetail", key = "#id")
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}