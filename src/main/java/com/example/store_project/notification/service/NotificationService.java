package com.example.store_project.notification.service;

import com.example.store_project.notification.model.Notification;
import com.example.store_project.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // يحفظ الإشعار بالقاعدة ثم يدفعه لحظياً لقناة الزبون
    public void sendNotification(String username, String message) {
        Notification notification = new Notification();
        notification.setUsername(username);
        notification.setMessage(message);
        notificationRepository.save(notification);

        push("/topic/orders/" + username, Map.of(
                "type", "ORDER_UPDATE",
                "message", message,
                "time", Instant.now().toString()));
    }

    // يدفع طلب جديد للوحة المسؤول
    public void notifyAdminNewOrder(Long orderId, String username) {
        push("/topic/admin/orders", Map.of(
                "type", "NEW_ORDER",
                "orderId", orderId,
                "username", username,
                "time", Instant.now().toString()));
    }

    public List<Notification> getUserNotifications(String username) {
        return notificationRepository.findByUsernameOrderByCreatedAtDesc(username);
    }

    // أي مشكلة بالدفع ما لازم تفشّل الطلب
    private void push(String destination, Map<String, Object> payload) {
        try {
            messagingTemplate.convertAndSend(destination, (Object) payload);            log.info("Pushed to {}", destination);
        } catch (RuntimeException e) {
            log.error("WebSocket push failed for {}", destination, e);
        }
    }
}