package com.example.User.Repository.SellerRepositories;

import com.example.User.Models.Seller.SellerStats;
import com.example.User.Service.BestSellerInfo;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.service.annotation.PatchExchange;

import java.util.List;
import java.util.Set;

@Repository
public interface Seller_Stat_Repo extends JpaRepository<SellerStats,String> {

    @Query("""
    SELECT new com.example.User.Service.BestSellerInfo(
        p.sellerId,
        p.name,
        p.phoneNumber,
        p.thumbnailImage,
        p.shopName,
        p.timings,
        p.street,
        p.city,
        p.state
    )
    FROM SellerStats s
    JOIN s.seller p
    WHERE s.geoHash IN :geohashes
    ORDER BY s.score DESC
    LIMIT 50
""")
    List<BestSellerInfo> findBestSeller(
            @Param("geohashes") List<String> geohashes
    );



}

