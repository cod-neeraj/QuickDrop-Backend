package com.example.User.Repository.OrderRepositories;

import com.example.User.DTO.ShowOrdersInDashBoard;
import com.example.User.Models.OrdersData.MainOrder;
import com.example.User.Service.Order.DeliveryBoyActiveDeliveryRepoData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainOrderRepo extends JpaRepository<MainOrder,String> {

    @Query("SELECT m FROM MainOrder m WHERE m.userId = :userId")
    List<MainOrder> findByUserId(@Param("userId") String userId);


    @Query("""
    SELECT m
    FROM MainOrder m
    JOIN m.sellerInfo s
    WHERE s.sellerId = :sellerId
""")
    List<MainOrder> findOrdersBySellerId(@Param("sellerId") String sellerId);

    @Query("SELECT m FROM MainOrder m WHERE m.order_id = :orderId")
    MainOrder findByOrderId(@Param("orderId") String orderId);


    @Query("""
   SELECT new com.example.User.DTO.ShowOrdersInDashBoard(
       m.order_id,
       m.orderStatus,
       m.paymentMethod,
       m.paymentStatus,
       m.userAddress,
       m.orderDate,
       m.totalAmount
   )
   FROM MainOrder m
   WHERE m.userId = :userId
   ORDER BY m.orderDate DESC
""")
    List<ShowOrdersInDashBoard> findUserOrdersSorted(@Param("userId") String userId, Pageable pageable);


    @Query("""
   SELECT new com.example.User.DTO.ShowOrdersInDashBoard(
       m.order_id,
       m.orderStatus,
       m.paymentMethod,
       m.paymentStatus,
       m.userAddress,
       m.orderDate,
       m.totalAmount
   )
   FROM MainOrder m
   WHERE m.userId = :userId
""")
    List<ShowOrdersInDashBoard> findUserOrdersData(@Param("userId") String userId);

    @Query("""
            SELECT new com.example.User.Service.Order.DeliveryBoyActiveDeliveryRepoData(
            m.order_id,m.orderStatus,m.userAddress,m.userId,m.username,m.orderDate,m.totalAmount)
            FROM MainOrder m WHERE m.order_id = :orderId
            """)
    DeliveryBoyActiveDeliveryRepoData findByOrderIdDeliveryBoy(@Param("orderId") String orderId);







}
