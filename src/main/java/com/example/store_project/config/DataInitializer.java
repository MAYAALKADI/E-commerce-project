package com.example.store_project.config;

import com.example.store_project.product.model.Product;
import com.example.store_project.product.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository) {
        return args -> {
            Product p1 = new Product();
            p1.setName("آيفون 15");
            p1.setPrice(999.0);
            p1.setCategory("إلكترونيات");
            p1.setImageUrl("https://via.placeholder.com/150");

            Product p2 = new Product();
            p2.setName("لاب توب ديل");
            p2.setPrice(750.0);
            p2.setCategory("كمبيوترات");
            p2.setImageUrl("https://via.placeholder.com/150");

            productRepository.save(p1);
            productRepository.save(p2);

            System.out.println("✅ تم إضافة المنتجات التجريبية بنجاح!");
        };
    }
}
