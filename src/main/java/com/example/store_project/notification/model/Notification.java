package com.example.store_project.notification.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // اسم المستخدم الموجه له الإشعار
    private String message; // محتوى الإشعار
    private boolean isRead = false; // هل تمت قراءته أم لا؟
    private LocalDateTime createdAt = LocalDateTime.now(); // وقت الإشعار
}