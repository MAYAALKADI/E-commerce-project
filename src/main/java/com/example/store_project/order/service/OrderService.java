package com.example.store_project.order.service;

import com.example.store_project.order.model.Order;
import com.example.store_project.order.repository.OrderRepository;
import com.example.store_project.notification.service.NotificationService; // أضفنا هذا السطر
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final NotificationService notificationService; // أضفنا هذا السطر

    // عدلنا الـ Constructor
    public OrderService(OrderRepository orderRepository, NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    public Order createOrder(Order order) {
        order.setStatus("PENDING");
        Order savedOrder = orderRepository.save(order);
        
        // إرسال إشعار عند إنشاء الطلب
        notificationService.sendNotification(savedOrder.getUsername(), "تم إنشاء طلبك بنجاح برقم: " + savedOrder.getId());
        
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
        
        // إرسال إشعار عند تغيير حالة الطلب
        notificationService.sendNotification(updatedOrder.getUsername(), "تم تغيير حالة طلبك رقم " + updatedOrder.getId() + " إلى: " + newStatus);
        
        return updatedOrder;
    }
}