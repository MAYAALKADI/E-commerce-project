package com.example.store_project.order.service;

import com.example.store_project.order.model.AdminStats;
import com.example.store_project.order.repository.OrderRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class OrderStatsService {

    private final OrderRepository orderRepository;

    public OrderStatsService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // الرد كله بينخزّن كنص JSON جاهز بالـ Redis، فما في serializer ولا Jackson
    @Cacheable(cacheNames = "adminStats", key = "'stats'")
    @Transactional(readOnly = true)
    public String getStatsJson() {
        return toJson(computeStats());
    }

    private AdminStats computeStats() {
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

        return new AdminStats(Instant.now().toString(),
                System.currentTimeMillis() - start, byStatus, top);
    }

    private String toJson(AdminStats s) {
        StringBuilder sb = new StringBuilder("{\"generatedAt\":\"").append(s.generatedAt())
                .append("\",\"queryTimeMs\":").append(s.queryTimeMs())
                .append(",\"byStatus\":[");
        for (int i = 0; i < s.byStatus().size(); i++) {
            var r = s.byStatus().get(i);
            if (i > 0) sb.append(',');
            sb.append("{\"status\":\"").append(r.status())
                    .append("\",\"orders\":").append(r.orders())
                    .append(",\"totalAmount\":").append(r.totalAmount())
                    .append(",\"avgAmount\":").append(r.avgAmount()).append('}');
        }
        sb.append("],\"topProducts\":[");
        for (int i = 0; i < s.topProducts().size(); i++) {
            var p = s.topProducts().get(i);
            if (i > 0) sb.append(',');
            sb.append("{\"productId\":").append(p.productId())
                    .append(",\"name\":\"").append(p.name().replace("\"", "\\\""))
                    .append("\",\"timesOrdered\":").append(p.timesOrdered()).append('}');
        }
        return sb.append("]}").toString();
    }
}