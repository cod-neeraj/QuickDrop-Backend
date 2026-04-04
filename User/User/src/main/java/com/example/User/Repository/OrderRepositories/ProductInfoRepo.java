package com.example.User.Repository.OrderRepositories;

import com.example.User.Models.OrdersData.ProductDetailsInfo;
import com.sun.jdi.LongValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ProductInfoRepo  extends JpaRepository<ProductDetailsInfo, Long> {
    @Query("""
    SELECT
        SUM(CASE
            WHEN p.sellerInfo.mainOrder.orderDate >= :startThisMonth
                 AND p.sellerInfo.mainOrder.orderDate < :startNextMonth
            THEN p.quantity ELSE 0 END),

        SUM(CASE
            WHEN p.sellerInfo.mainOrder.orderDate >= :startLastMonth
                 AND p.sellerInfo.mainOrder.orderDate < :startThisMonth
            THEN p.quantity ELSE 0 END)

    FROM ProductDetailsInfo p
    JOIN p.sellerInfo s
    WHERE s.sellerId = :sellerId
""")
    Object[] getMonthlyProductStats(
            String sellerId,
            LocalDate startThisMonth,
            LocalDate startNextMonth,
            LocalDate startLastMonth
    );
}
