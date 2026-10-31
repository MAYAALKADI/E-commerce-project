package com.example.store_project.notification.service;

import com.example.store_project.notification.model.Notification;
import com.example.store_project.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // دالة لإنشاء إشعار جديد (سيتم استدعاؤها من OrderService)
    public void sendNotification(String username, String message) {
        Notification notification = new Notification();
        notification.setUsername(username);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    // دالة لجلب إشعارات المستخدم
    public List<Notification> getUserNotifications(String username) {
        return notificationRepository.findByUsernameOrderByCreatedAtDesc(username);
    }
}