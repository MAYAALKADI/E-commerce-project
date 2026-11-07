package com.example.inventory.repository;

import com.example.inventory.model.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockRepository extends JpaRepository<StockItem, Long> {

    // الحجز الذري: بينقص فقط إذا الكمية كافية، وبيرجع عدد الصفوف المتأثرة (0 = فشل)
    @Modifying
    @Query("UPDATE StockItem s SET s.quantity = s.quantity - :qty WHERE s.productId = :id AND s.quantity >= :qty")
    int decrease(@Param("id") Long id, @Param("qty") int qty);

    @Modifying
    @Query("UPDATE StockItem s SET s.quantity = s.quantity + :qty WHERE s.productId = :id")
    int increase(@Param("id") Long id, @Param("qty") int qty);
}