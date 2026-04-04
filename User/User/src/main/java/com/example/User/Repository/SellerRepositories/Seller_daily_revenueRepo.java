package com.example.User.Repository.SellerRepositories;

import com.example.User.Models.Seller.Seller_daily_revenue;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface Seller_daily_revenueRepo extends JpaRepository<Seller_daily_revenue,Long> {

    @Query(value = "SELECT SUM(r.revenue) " +
            "FROM Seller_daily_revenue r " +
            "WHERE r.seller.id = :sellerId " +
            "AND r.date =:date")
    Double getThisMonthRevenue(@Param("sellerId") Long sellerId, @Param("date")LocalDate date);


    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO seller_daily_revenue
        (order_count, date, revenue, seller_id)
    VALUES
        (1, CURRENT_DATE, :amount, :sellerId)
    ON CONFLICT (seller_id, date)
    DO UPDATE
    SET order_count = seller_daily_revenue.order_count + 1,
        revenue = seller_daily_revenue.revenue + EXCLUDED.revenue
    """, nativeQuery = true)
    int upsertDailyRevenue(@Param("sellerId") Long sellerId,
                            @Param("amount") Double amount);


    @Query("""
    SELECT COUNT(s) > 0 FROM Seller_daily_revenue s
    WHERE s.date = :date AND s.seller.sellerId = :sellerId
""")
    boolean existsByDateAndSellerId(LocalDate date, String sellerId);

    @Modifying
    @Query("""
    UPDATE Seller_daily_revenue s
    SET
        s.revenue = s.revenue + :amount,
        s.orderCount = s.orderCount + 1,
        s.averageOrderValue = ((s.revenue * s.orderCount) + :amount) / (s.orderCount + 1)
    WHERE s.date = :date AND s.seller.sellerId = :sellerId
""")
    int updateRevenue(LocalDate date, String sellerId, BigDecimal amount);
        /*

    private LocalDate date;

    private Double revenue;
    private Integer orderCount;
    private Double averageOrderValue;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    @JsonBackReference
    private Seller_Info seller;

         */

}