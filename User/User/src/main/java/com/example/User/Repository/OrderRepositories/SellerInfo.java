package com.example.User.Repository.OrderRepositories;

import com.example.User.DTO.SellerDashBoardOrderList;
import com.example.User.Models.OrdersData.DeliveryBoyStatus;
import com.example.User.Models.OrdersData.ProductDetailsInfo;
import com.example.User.Models.Seller.SellerStatus;
import com.example.User.Service.Order.DeliveryBoyActiveDeliverySellerInfo;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SellerInfo extends JpaRepository<com.example.User.Models.OrdersData.SellerInfo,Long> {

    @Query("""
  SELECT new com.example.User.DTO.SellerDashBoardOrderList(m.order_id,m.username,m.totalAmount,m.deliveryBoyName,s.orderStatus)
  FROM com.example.User.Models.OrdersData.SellerInfo s
 JOIN s.mainOrder m
  WHERE s.sellerId = :id
""")
    List<SellerDashBoardOrderList> findOrderListBySellerPhoneNumber(@Param("id") String id);

    @Query("""
        SELECT COALESCE(SUM(m.totalAmount), 0)
        FROM com.example.User.Models.OrdersData.SellerInfo s
        JOIN s.mainOrder m
        WHERE s.sellerId = :sellerId
        AND m.orderDate >= :start
        AND m.orderDate < :end
    """)
    Double getRevenueBetweenDates(
            @Param("sellerId") Long sellerId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    @Query("""
  SELECT p
  FROM com.example.User.Models.OrdersData.SellerInfo s
 JOIN s.mainOrder m
 JOIN s.productDetailsInfo p
  WHERE s.sellerId = :phoneNumber AND m.order_id = :orderId
""")
    List<ProductDetailsInfo> findDetailorder(@Param("orderId") String orderId,@Param("phoneNumber") String phoneNumber);

    @Query("""
            SELECT m.username,m.totalAmount,s.orderStatus FROM com.example.User.Models.OrdersData.SellerInfo s
            JOIN s.mainOrder m
            WHERE s.sellerId = :phoneNumber
          """)
    List<Object[]> findTop5Orders(@Param("phoneNumber") String phoneNumber, Pageable pageable);

@Transactional
    @Modifying
    @Query("""
  UPDATE com.example.User.Models.OrdersData.SellerInfo s
  SET s.orderStatus = :status
  WHERE s.sellerId = :phoneNumber
    AND s.mainOrder.order_id = :orderId
""")
    int updateStatus(@Param("orderId") String orderId,
                     @Param("phoneNumber") String phoneNumber,
                     @Param("status") SellerStatus status);



@Query ("""
        SELECT new com.example.User.Service.Order.DeliveryBoyActiveDeliverySellerInfo(
        s.sellerId,s.name,s.shopName,s.street,s.city,s.state,s.orderStatus,s.deliveryBoyStatus)
        FROM com.example.User.Models.OrdersData.SellerInfo s WHERE s.mainOrder.order_id = :orderId
        """)

List<DeliveryBoyActiveDeliverySellerInfo> findByOrderId(@Param("orderId") String orderId);


    @Query(value = """
    SELECT
        COUNT(*) FILTER (
            WHERE o.order_date >= DATE_TRUNC('month', CURRENT_DATE)
        ),
        COUNT(*) FILTER (
            WHERE o.order_date >= CURRENT_DATE
        ),
        COUNT(*) FILTER (
            WHERE o.order_date >= DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
              AND o.order_date < DATE_TRUNC('month', CURRENT_DATE)
        ),
        COUNT(*) FILTER (
            WHERE s.order_status = :status
        )
        FROM orders.seller_info s
      JOIN orders.main_order o  ON o.order_id = s.main_order_id
    WHERE s.seller_id = :sellerId
    """,nativeQuery = true)
    Object[] findMainDashboardPageStats(@Param("sellerId") String sellerId,
                                        @Param("status") SellerStatus status);



    @Transactional
    @Modifying
    @Query("""
  UPDATE com.example.User.Models.OrdersData.SellerInfo s
  SET s.deliveryBoyStatus = :status
  WHERE s.sellerId = :phoneNumber
    AND s.mainOrder.order_id = :orderId
""")
    int updateDeliveryBoyStatus(@Param("orderId") String orderId,
                     @Param("phoneNumber") String phoneNumber,
                     @Param("status") DeliveryBoyStatus status);


    @Query("""
            SELECT s.mainOrder.totalAmount FROM  com.example.User.Models.OrdersData.SellerInfo s
            WHERE s.sellerId = :phoneNumber
            AND s.mainOrder.order_id = :orderId
            AND s.orderStatus =:sellerStatus
            AND s.deliveryBoyStatus = :deliveryBoyStatus
            """)

    Double fetchAmountWhichOrderAreDone(@Param("phoneNumber") String phoneNumber,
                                        @Param("orderId") String orderId,
                                        @Param("sellerStatus") SellerStatus sellerStatus,
                                        @Param("deliveryBoyStatus") DeliveryBoyStatus deliveryBoyStatus);



    @Query("""
    SELECT
        COALESCE(SUM(CASE
            WHEN o.orderDate >= :startThisMonth AND o.orderDate < :startNextMonth
            THEN o.totalAmount ELSE 0 END), 0),

        COALESCE(SUM(CASE
            WHEN o.orderDate >= :startLastMonth AND o.orderDate < :startThisMonth
            THEN o.totalAmount ELSE 0 END), 0),

        COUNT(CASE
            WHEN o.orderDate >= :startThisMonth AND o.orderDate < :startNextMonth
            THEN o.id ELSE NULL END),

        COUNT(CASE
            WHEN o.orderDate >= :startLastMonth AND o.orderDate < :startThisMonth
            THEN o.id ELSE NULL END)

    FROM com.example.User.Models.OrdersData.SellerInfo s
    JOIN s.mainOrder o
    WHERE s.sellerId = :sellerId
""")
    Object[] getMonthlyStats(
            String sellerId,
            LocalDate startThisMonth,
            LocalDate startNextMonth,
            LocalDate startLastMonth
    );

    @Query("""
SELECT
    DATE(o.orderDate),
    SUM(o.totalAmount)
FROM com.example.User.Models.OrdersData.SellerInfo s
JOIN s.mainOrder o
WHERE s.sellerId = :sellerId
AND o.orderDate >= :startDate
AND o.orderDate <= :endDate
GROUP BY DATE(o.orderDate)
""")
    List<Object[]> getDailySales(String sellerId, LocalDate startDate, LocalDate endDate);

    @Query("""
SELECT
    FUNCTION('MONTH', o.orderDate),
    FUNCTION('YEAR', o.orderDate),
    SUM(o.totalAmount)
FROM com.example.User.Models.OrdersData.SellerInfo s
JOIN s.mainOrder o
WHERE s.sellerId = :sellerId
AND o.orderDate BETWEEN :startDate AND :endDate
GROUP BY FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate)
ORDER BY FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate)
""")
    List<Object[]> getMonthlyGraphSales(String sellerId, LocalDate startDate, LocalDate endDate);

    @Query("""
SELECT
    DATE(o.orderDate),
    COUNT(o.id)
FROM com.example.User.Models.OrdersData.SellerInfo s
JOIN s.mainOrder o
WHERE s.sellerId = :sellerId
AND o.orderDate >= :startDate
AND o.orderDate <= :endDate
GROUP BY DATE(o.orderDate)
""")
    List<Object[]> getOrderDaily(String sellerId, LocalDate startDate, LocalDate endDate);

    @Query("""
SELECT
    FUNCTION('MONTH', o.orderDate),
    FUNCTION('YEAR', o.orderDate),
    COUNT(o.id)
FROM com.example.User.Models.OrdersData.SellerInfo s
JOIN s.mainOrder o
WHERE s.sellerId = :sellerId
AND o.orderDate BETWEEN :startDate AND :endDate
GROUP BY FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate)
ORDER BY FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate)
""")
    List<Object[]> getOrderMonthlyGraph(String sellerId, LocalDate startDate, LocalDate endDate);




    @Query("""
    SELECT MIN(s.orderStatus)
    FROM com.example.User.Models.OrdersData.SellerInfo s
    WHERE s.mainOrder.order_id = :orderId
""")
    Integer findOrderStatus(@Param("orderId") String orderId);
}



