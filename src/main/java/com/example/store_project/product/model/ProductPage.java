package com.example.store_project.product.model;

import java.io.Serializable;
import java.util.List;

public record ProductPage(List<Product> content, int number, int size,
                          long totalElements, int totalPages) implements Serializable {}