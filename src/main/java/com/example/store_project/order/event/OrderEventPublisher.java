package com.example.store_project.order.event;

import com.example.store_project.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY,
                    event);
            log.info("Published OrderCreatedEvent for order {}", event.orderId());
        } catch (RuntimeException e) {
            // لو RabbitMQ واقف: الطلب انحفظ أصلاً، ما منفشّله الطلب بسبب إيميل
            log.error("Failed to publish event for order {}", event.orderId(), e);
        }
    }
}