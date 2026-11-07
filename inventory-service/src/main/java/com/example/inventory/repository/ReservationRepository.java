package com.example.inventory.repository;

import com.example.inventory.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByOrderId(String orderId);

    List<Reservation> findByOrderIdAndReleasedFalse(String orderId);
}