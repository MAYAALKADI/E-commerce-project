package com.example.store_project.order.service;

import com.example.store_project.config.RecentWriteTracker;
import com.example.store_project.config.RoutingContext;
import com.example.store_project.inventory.client.InventoryClient;
import com.example.store_project.notification.service.NotificationService;
import com.example.store_project.order.event.OrderCreatedEvent;
import com.example.store_project.order.event.OrderEventPublisher;
import com.example.store_project.order.model.Order;
import com.example.store_project.order.repository.OrderRepository;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
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
    private final RecentWriteTracker recentWrites;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository,
                        NotificationService notificationService,
                        InventoryClient inventoryClient,
                        RecentWriteTracker recentWrites,
                        OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
        this.inventoryClient = inventoryClient;
        this.recentWrites = recentWrites;
        this.eventPublisher = eventPublisher;
    }

    public Order createOrder(Order order) {
        if (order.getProductIds() == null || order.getProductIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Order must contain at least one product");
        }

        Map<Long, Integer> items = new LinkedHashMap<>();
        for (Long productId : order.getProductIds()) {
            items.merge(productId, 1, Integer::sum);
        }

        String reservationId = UUID.randomUUID().toString();
        inventoryClient.reserve(reservationId, items);

        Order savedOrder;
        try {
            order.setStatus("PENDING");
            savedOrder = orderRepository.save(order);
        } catch (RuntimeException e) {
            try {
                inventoryClient.release(reservationId);
            } catch (RuntimeException releaseError) {
                log.error("Failed to release reservation {}", reservationId, releaseError);
            }
            throw e;
        }

        recentWrites.markWritten(savedOrder.getUsername());

        // إشعار الزبون (محفوظ + لحظي)
        notificationService.sendNotification(savedOrder.getUsername(),
                "تم إنشاء طلبك بنجاح برقم: " + savedOrder.getId());

        // لوحة المسؤول: يظهر الطلب فوراً بدون refresh
        notificationService.notifyAdminNewOrder(savedOrder.getId(), savedOrder.getUsername());

        // الإيميل: حدث للـ Worker، والزبون بياخد الرد فوراً
        eventPublisher.publishOrderCreated(new OrderCreatedEvent(
                savedOrder.getId(), savedOrder.getUsername(), Instant.now()));

        return savedOrder;
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByUsername(String username) {
        boolean usePrimary = recentWrites.hasRecentWrite(username);
        if (usePrimary) {
            RoutingContext.forcePrimary();
        }
        try {
            List<Order> orders = orderRepository.findByUsername(username);
            orders.forEach(o -> Hibernate.initialize(o.getProductIds()));
            return orders;
        } finally {
            if (usePrimary) {
                RoutingContext.clear();
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // بدون readOnly عن قصد: القراءة هون مقدّمة لتعديل، فلازم من الـ Primary
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order updateStatus(Order order, String newStatus) {
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        recentWrites.markWritten(updatedOrder.getUsername());

        // صفحة الزبون: تتغيّر الحالة لحظياً (sendNotification بيدفعها للقناة)
        notificationService.sendNotification(updatedOrder.getUsername(),
                "تم تغيير حالة طلبك رقم " + updatedOrder.getId() + " إلى: " + newStatus);

        return updatedOrder;
    }
}