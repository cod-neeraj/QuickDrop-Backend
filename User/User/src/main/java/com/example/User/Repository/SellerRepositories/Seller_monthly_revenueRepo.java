package com.example.User.Repository.SellerRepositories;

import com.example.User.Models.Seller.Seller_monthly_revenue;
import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Seller_monthly_revenueRepo extends JpaRepository<Seller_monthly_revenue,Long>{

    @Query("SELECT SUM(r.revenue) FROM Seller_monthly_revenue r " +
            "WHERE r.seller.id = :sellerId " +
            "AND r.year = :year AND r.month = :month ")
    Double getRevenueForMonth(
            @Param("sellerId") Long sellerId,
            @Param("year") int year,
            @Param("month") int month
    );
}
