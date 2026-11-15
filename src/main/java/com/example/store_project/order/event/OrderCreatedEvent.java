package com.example.store_project.order.event;

import java.time.Instant;

// الحدث = رسالة صغيرة بتقول "صار شي". record = كلاس ثابت (immutable) وقصير
public record OrderCreatedEvent(
        Long orderId,
        String username,
        Instant createdAt
) {}