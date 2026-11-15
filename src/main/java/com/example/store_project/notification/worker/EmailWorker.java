package com.example.store_project.notification.worker;

import com.example.store_project.config.RabbitMQConfig;
import com.example.store_project.notification.service.EmailService;
import com.example.store_project.order.event.OrderCreatedEvent;
import com.example.store_project.user.model.User;
import com.example.store_project.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailWorker {

    private static final Logger log = LoggerFactory.getLogger(EmailWorker.class);

    private final UserRepository userRepository;
    private final EmailService emailService;

    public EmailWorker(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    // id: حتى نقدر نوقف/نشغّل الـ Worker لاحقاً بالاختبار
    @RabbitListener(id = "emailWorker", queues = RabbitMQConfig.QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Worker received event for order {}", event.orderId());

        User user = userRepository.findByUsername(event.username())
                .orElseThrow(() -> new IllegalStateException(
                        "User not found: " + event.username()));

        emailService.sendOrderConfirmation(user.getEmail(), event.orderId());
        // وصلنا هون بدون خطأ = Spring بيبعت ACK تلقائياً
    }
}