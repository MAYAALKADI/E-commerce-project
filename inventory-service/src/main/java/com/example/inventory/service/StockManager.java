package com.example.inventory.service;

import com.example.inventory.grpc.Item;
import com.example.inventory.model.Reservation;
import com.example.inventory.model.StockItem;
import com.example.inventory.repository.ReservationRepository;
import com.example.inventory.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockManager {

    private final StockRepository stockRepo;
    private final ReservationRepository reservationRepo;

    @Transactional
    public void register(Long productId, int qty) {
        stockRepo.save(new StockItem(productId, qty));
    }

    public Optional<StockItem> check(Long productId) {
        return stockRepo.findById(productId);
    }

    // أي RuntimeException هون = Rollback لكل الحجز (all-or-nothing)
    @Transactional
    public void reserve(String orderId, List<Item> items) {
        if (reservationRepo.existsByOrderId(orderId)) {
            return; // نفس الطلب انبعت مرتين، ما نحجز مرتين
        }

        for (Item i : items) {
            int updated = stockRepo.decrease(i.getProductId(), i.getQuantity());
            if (updated == 0) {
                throw new IllegalStateException(
                        "Insufficient stock for product " + i.getProductId());
            }
            Reservation r = new Reservation();
            r.setOrderId(orderId);
            r.setProductId(i.getProductId());
            r.setQuantity(i.getQuantity());
            reservationRepo.save(r);
        }
    }

    @Transactional
    public void release(String orderId) {
        for (Reservation r : reservationRepo.findByOrderIdAndReleasedFalse(orderId)) {
            stockRepo.increase(r.getProductId(), r.getQuantity());
            r.setReleased(true);
        }
    }
}