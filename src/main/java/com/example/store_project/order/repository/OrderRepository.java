package com.example.store_project.order.repository;

import com.example.store_project.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUsername(String username);

    interface StatusRow {
        String getStatus();
        Long getOrderCount();
        Double getTotalAmount();
        Double getAvgAmount();
    }

    interface TopProductRow {
        Number getProductId();
        String getName();
        Number getTimesOrdered();
    }

    @Query("SELECT o.status AS status, COUNT(o) AS orderCount, SUM(o.totalAmount) AS totalAmount, "
            + "AVG(o.totalAmount) AS avgAmount FROM Order o GROUP BY o.status ORDER BY o.status")
    List<StatusRow> statsByStatus();

    @Query(value = "SELECT p.id AS productId, p.name AS name, COUNT(*) AS timesOrdered "
            + "FROM order_product_ids op JOIN products p ON p.id = op.product_ids "
            + "GROUP BY p.id, p.name ORDER BY timesOrdered DESC LIMIT 10", nativeQuery = true)
    List<TopProductRow> topProducts();
}