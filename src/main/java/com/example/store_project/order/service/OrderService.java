package com.example.store_project.order.service;

import com.example.store_project.inventory.client.InventoryClient;
import com.example.store_project.notification.service.NotificationService;
import com.example.store_project.order.model.Order;
import com.example.store_project.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private final InventoryClient inventoryClient;

    public OrderService(OrderRepository orderRepository,
                        NotificationService notificationService,
                        InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
        this.inventoryClient = inventoryClient;
    }

    public Order createOrder(Order order) {
        if (order.getProductIds() == null || order.getProductIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Order must contain at least one product");
        }

        // كل تكرار لنفس الـ id = قطعة وحدة: [5,5,7] => {5=2, 7=1}
        Map<Long, Integer> items = new LinkedHashMap<>();
        for (Long productId : order.getProductIds()) {
            items.merge(productId, 1, Integer::sum);
        }

        // 1) حجز كل المنتجات بنداء واحد. إذا فشل بيرمي exception والطلب ما بينحفظ
        String reservationId = UUID.randomUUID().toString();
        inventoryClient.reserve(reservationId, items);

        // 2) حفظ الطلب. إذا فشل، منرجّع الكمية المحجوزة (Compensation)
        Order savedOrder;
        try {
            order.setStatus("PENDING");
           // if (true) throw new RuntimeException("Simulated DB failure");
            savedOrder = orderRepository.save(order);
        } catch (RuntimeException e) {
            try {
                inventoryClient.release(reservationId);
            } catch (RuntimeException releaseError) {
                log.error("Failed to release reservation {}", reservationId, releaseError);
            }
            throw e;
        }

        // 3) الإشعار بعد ما الطلب انحفظ فعلاً
        notificationService.sendNotification(savedOrder.getUsername(),
                "تم إنشاء طلبك بنجاح برقم: " + savedOrder.getId());

        return savedOrder;
    }

    public List<Order> getOrdersByUsername(String username) {
        return orderRepository.findByUsername(username);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order updateStatus(Order order, String newStatus) {
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        notificationService.sendNotification(updatedOrder.getUsername(),
                "تم تغيير حالة طلبك رقم " + updatedOrder.getId() + " إلى: " + newStatus);

        return updatedOrder;
    }
}