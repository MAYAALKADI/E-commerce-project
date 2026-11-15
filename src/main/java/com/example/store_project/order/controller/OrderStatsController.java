package com.example.store_project.order.controller;

import com.example.store_project.order.service.OrderStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders/admin/stats")
public class OrderStatsController {

    private final OrderStatsService statsService;

    public OrderStatsController(OrderStatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<String> getStats() {
        return ResponseEntity.ok(statsService.getStatsJson());
    }
}