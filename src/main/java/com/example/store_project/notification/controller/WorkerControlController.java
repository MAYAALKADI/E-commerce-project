package com.example.store_project.notification.controller;

import org.springframework.amqp.core.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// المسار تحت /api/v1/orders/admin/** يعني ADMIN فقط (من SecurityConfig)
@RestController
@RequestMapping("/api/v1/orders/admin/worker")
public class WorkerControlController {

    private final RabbitListenerEndpointRegistry registry;

    public WorkerControlController(RabbitListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    @PostMapping("/stop")
    public Map<String, Object> stop() {
        container().stop();
        return Map.of("worker", "emailWorker", "running", container().isRunning());
    }

    @PostMapping("/start")
    public Map<String, Object> start() {
        container().start();
        return Map.of("worker", "emailWorker", "running", container().isRunning());
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of("worker", "emailWorker", "running", container().isRunning());
    }

    private MessageListenerContainer container() {
        return registry.getListenerContainer("emailWorker");
    }
}