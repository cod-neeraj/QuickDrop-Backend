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


}

