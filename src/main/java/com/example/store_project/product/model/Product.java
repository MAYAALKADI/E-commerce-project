package com.example.store_project.product.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;
    
    @Column(columnDefinition = "TEXT")
    private String description; 
    
    private String category; 
    private String imageUrl; 
}