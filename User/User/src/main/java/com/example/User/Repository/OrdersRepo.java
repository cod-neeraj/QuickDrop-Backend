package com.example.User.Repository;

import com.example.User.DataToShow.OrderDashBoard;
import com.example.User.Models.Orders;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable; // ✅ CORRECT
import java.util.List;

public interface OrdersRepo extends JpaRepository<Orders,Long> {
//    @Query("SELECT new com.example.User.DataToShow.OrderDashBoard(o.orderId, o.orderDate, o.totalAmount, o.orderStatus) " +
//            "FROM Orders o WHERE o.user.id = :userId ORDER BY o.orderDate DESC")
//    List<OrderDashBoard> findTop3ByUserIdOrderByOrderDateDesc(@Param("userId") Long userId, Pageable pageable);

}
