package com.example.Delivery.Repository;

import com.example.Delivery.Models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface OrdersRepo extends JpaRepository<Orders,String> {

    @Query("""
      SELECT
      COALESCE(SUM(d.orderEarnings), 0),
      COUNT(d)
      FROM Orders d
      WHERE d.user.id = :deliveryBoyId
      AND d.orderStatus = 'DELIVERED'
      AND d.deliveredAt >= :startOfToday
      AND d.deliveredAt <= :now
""")
    Object[] getTodayStats(
            @Param("deliveryBoyId") String deliveryBoyId,
            @Param("startOfToday") LocalDateTime startOfToday,
            @Param("now") LocalDateTime now
    );
    @Query("""
     SELECT
     COALESCE(SUM(d.orderEarnings), 0)
     FROM Orders d
     WHERE d.user.id = :deliveryBoyId
     AND d.orderStatus = 'DELIVERED'
     AND d.deliveredAt >= :startOfWeek
     AND d.deliveredAt <= :now
""")
    Double getWeekEarnings(
            @Param("deliveryBoyId") String deliveryBoyId,
            @Param("startOfWeek") LocalDateTime startOfWeek,
            @Param("now") LocalDateTime now
    );

}
