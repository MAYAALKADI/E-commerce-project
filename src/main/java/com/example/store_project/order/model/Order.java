package com.example.store_project.order.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.BatchSize;


@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_orders_username", columnList = "username")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; 
    private Double totalAmount; 
    private String status; 
    private LocalDateTime orderDate = LocalDateTime.now(); 

    @ElementCollection
    @BatchSize(size = 100)
    private List<Long> productIds;
}