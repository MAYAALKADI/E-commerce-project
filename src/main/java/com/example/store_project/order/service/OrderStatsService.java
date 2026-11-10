package com.example.store_project.order.service;

import com.example.store_project.order.model.AdminStats;
import com.example.store_project.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.time.Instant;
import java.util.List;

@Service
public class OrderStatsService {

    private final OrderRepository orderRepository;

    public OrderStatsService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // readOnly = true  ==> بيروح للـ Replica (الـ Routing)
    @Cacheable(cacheNames = "adminStats", key = "'stats'")
    @Transactional(readOnly = true)
    public AdminStats getStats() {
        long start = System.currentTimeMillis();

        List<AdminStats.StatusStat> byStatus = orderRepository.statsByStatus().stream()
                .map(r -> new AdminStats.StatusStat(
                        r.getStatus(),
                        r.getOrderCount() == null ? 0 : r.getOrderCount(),
                        r.getTotalAmount() == null ? 0 : r.getTotalAmount(),
                        r.getAvgAmount() == null ? 0 : r.getAvgAmount()))
                .toList();

        List<AdminStats.TopProduct> top = orderRepository.topProducts().stream()
                .map(r -> new AdminStats.TopProduct(
                        r.getProductId().longValue(),
                        r.getName(),
                        r.getTimesOrdered().longValue()))
                .toList();

                return new AdminStats(java.time.Instant.now().toString(), System.currentTimeMillis() - start, byStatus, top);
        }
}