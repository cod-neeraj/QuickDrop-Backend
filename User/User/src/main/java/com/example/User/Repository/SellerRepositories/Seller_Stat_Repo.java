package com.example.User.Repository.SellerRepositories;

import com.example.User.Models.Seller.SellerStats;
import com.example.User.Service.BestSellerInfo;
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
        s.sellerId,
        s.name,
        s.thumbnailImage,
        s.shopName,
        s.street,
        s.city
    )
    FROM SellerInfo s
    JOIN s.sellerStats p
    WHERE s.geohash IN :geohashes
    ORDER BY p.score DESC
""")
    List<BestSellerInfo> findBestSeller(
            @Param("geohashes") List<String> geohashes
    );


}

