package com.example.store_project.config;

import com.example.store_project.inventory.client.InventoryClient;
import com.example.store_project.product.model.Product;
import com.example.store_project.product.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class DataInitializer {

    
    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository,
                                   InventoryClient inventoryClient) {
        return args -> {
            if (productRepository.count() > 0) {
            return;
                                               }
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

            // تسجيل المخزون بالمستودع. إذا المستودع مش جاهز، المتجر لازم يضل يشتغل
            try {
                inventoryClient.registerProduct(p1.getId(), 10);
                inventoryClient.registerProduct(p2.getId(), 10);
                System.out.println("✅ تم تسجيل مخزون المنتجات التجريبية بالمستودع");
            } catch (RuntimeException e) {
                System.out.println("⚠️ تعذّر تسجيل المخزون (المستودع غير متاح): " + e.getMessage());
            }

            System.out.println("✅ تم إضافة المنتجات التجريبية بنجاح!");
        };
    }
}