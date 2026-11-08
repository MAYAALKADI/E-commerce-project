package com.example.store_project.config;

import com.example.store_project.inventory.client.InventoryClient;
import com.example.store_project.order.model.Order;
import com.example.store_project.order.repository.OrderRepository;
import com.example.store_project.product.model.Product;
import com.example.store_project.product.repository.ProductRepository;
import com.example.store_project.user.model.User;
import com.example.store_project.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// بيشتغل فقط إذا seed.enabled=true (متغير البيئة SEED_ENABLED=true)
@Component
@ConditionalOnProperty(name = "seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private static final int USERS = 1_000;
    private static final int PRODUCTS = 2_000;
    private static final int ORDERS = 100_000;
    private static final int CHUNK = 1_000;
    private static final int PRODUCTS_IN_INVENTORY = 50;

    private static final String[] CATEGORIES =
            {"electronics", "computers", "clothes", "books", "home", "sports", "toys", "beauty"};
    private static final String[] STATUSES =
            {"PENDING", "SHIPPED", "DELIVERED", "CANCELLED"};

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate tx;

    public DataSeeder(UserRepository userRepository,
                      ProductRepository productRepository,
                      OrderRepository orderRepository,
                      InventoryClient inventoryClient,
                      PasswordEncoder passwordEncoder,
                      PlatformTransactionManager txManager) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
        this.passwordEncoder = passwordEncoder;
        this.tx = new TransactionTemplate(txManager);
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("seeduser1").isPresent()) {
            System.out.println("⏭️ Seed data already exists, skipping.");
            return;
        }

        Random random = new Random(42);
        long start = System.currentTimeMillis();

        // 1) المستخدمين (كلمة السر لكلهم: Seed@123، بتشفير واحد فقط لأنو bcrypt بطيء)
        String hash = passwordEncoder.encode("Seed@123");
        List<String> usernames = new ArrayList<>();
        for (int from = 1; from <= USERS; from += CHUNK) {
            final int f = from;
            tx.executeWithoutResult(s -> {
                List<User> batch = new ArrayList<>();
                for (int i = f; i < f + CHUNK && i <= USERS; i++) {
                    batch.add(new User("seeduser" + i, "seeduser" + i + "@test.com", hash, "USER"));
                }
                userRepository.saveAll(batch);
            });
        }
        for (int i = 1; i <= USERS; i++) usernames.add("seeduser" + i);
        System.out.println("✅ Seed: users done");

        // 2) المنتجات
        List<Long> productIds = new ArrayList<>();
        for (int from = 1; from <= PRODUCTS; from += CHUNK) {
            final int f = from;
            List<Product> saved = tx.execute(s -> {
                List<Product> batch = new ArrayList<>();
                for (int i = f; i < f + CHUNK && i <= PRODUCTS; i++) {
                    Product p = new Product();
                    p.setName("Seed Product " + i);
                    p.setPrice(5.0 + random.nextInt(995));
                    p.setDescription("Generated product number " + i);
                    p.setCategory(CATEGORIES[random.nextInt(CATEGORIES.length)]);
                    p.setImageUrl("https://via.placeholder.com/150");
                    batch.add(p);
                }
                return productRepository.saveAll(batch);
            });
            saved.forEach(p -> productIds.add(p.getId()));
        }
        System.out.println("✅ Seed: products done");

        // تسجيل أول 50 منتج بالمستودع (للاختبارات اللي بتحتاج طلب فعلي)
        try {
            for (int i = 0; i < PRODUCTS_IN_INVENTORY && i < productIds.size(); i++) {
                inventoryClient.registerProduct(productIds.get(i), 1000);
            }
            System.out.println("✅ Seed: inventory registered for first " + PRODUCTS_IN_INVENTORY + " products");
        } catch (RuntimeException e) {
            System.out.println("⚠️ Inventory not available, skipped: " + e.getMessage());
        }

        // 3) الطلبات (مباشرة على القاعدة، بدون المرور بالمستودع)
        LocalDateTime now = LocalDateTime.now();
        for (int from = 1; from <= ORDERS; from += CHUNK) {
            tx.executeWithoutResult(s -> {
                List<Order> batch = new ArrayList<>();
                for (int i = 0; i < CHUNK; i++) {
                    Order o = new Order();
                    o.setUsername(usernames.get(random.nextInt(usernames.size())));
                    o.setStatus(STATUSES[random.nextInt(STATUSES.length)]);
                    o.setTotalAmount(10.0 + random.nextInt(990));
                    o.setOrderDate(now.minusMinutes(random.nextInt(60 * 24 * 365)));
                    List<Long> items = new ArrayList<>();
                    int n = 1 + random.nextInt(4);
                    for (int k = 0; k < n; k++) {
                        items.add(productIds.get(random.nextInt(productIds.size())));
                    }
                    o.setProductIds(items);
                    batch.add(o);
                }
                orderRepository.saveAll(batch);
            });
            if ((from / CHUNK) % 10 == 0) {
                System.out.println("   Seed: orders " + (from + CHUNK - 1) + " / " + ORDERS);
            }
        }

        System.out.println("✅ Seed finished in " + (System.currentTimeMillis() - start) / 1000 + " seconds");
    }
}