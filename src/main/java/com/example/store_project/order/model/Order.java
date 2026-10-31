package com.example.store_project.order.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
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
    private List<Long> productIds;
}