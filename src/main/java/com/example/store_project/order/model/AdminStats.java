package com.example.store_project.order.model;

import java.util.List;

public record AdminStats(
        String generatedAt,
        long queryTimeMs,
        List<StatusStat> byStatus,
        List<TopProduct> topProducts) {

    public record StatusStat(String status, long orders, double totalAmount, double avgAmount) {}

    public record TopProduct(long productId, String name, long timesOrdered) {}
}