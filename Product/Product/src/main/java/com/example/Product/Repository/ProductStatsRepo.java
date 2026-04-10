package com.example.Product.Repository;

import com.example.Product.DTO.RecommendedProductCard;
import com.example.Product.Model.ProductData.Product;
import com.example.Product.Model.ProductData.ProductStats;
import com.example.Product.Model.ProductSearchAbleObject;
import com.example.Product.Model.RecommendationDataDTO;
import com.example.Product.Service.BestProductInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductStatsRepo extends JpaRepository<ProductStats,String> {

//    List<ProductStats> findTop10ByGeohashCodeOrderByScoreDesc(String geohashCode);


    @Query("""
SELECT new com.example.Product.Model.RecommendationDataDTO(
p.productId,p.name,p.type,p.brand,p.productCategory,p.ratings,p.quantity,p.defaultImageUrl)
FROM ProductStats s
JOIN s.product p
WHERE s.geohashCode = :geoHashCode
ORDER BY s.score DESC
""")
    Page<RecommendationDataDTO> findTopProductsByGeohashCode(
            @Param("geoHashCode") String geoHashCode,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE ProductStats p SET p.totalOrders = p.totalOrders + 1 WHERE p.productId IN :productIds")
    void incrementOrderCountBulk(@Param("productIds") List<String> productIds);

    @Query("""
SELECT new com.example.Product.Service.BestProductInfo(
    p.productId,
    p.name,
    p.type,
    p.brand,
    p.productCategory,
    p.defaultImageUrl,
    p.defaultPrice,
    p.ratings,
    s.score
)
FROM ProductStats s
JOIN s.product p
WHERE p.geohash IN :geohashes
""")
    List<BestProductInfo> findBestProducts(
            @Param("geohashes") List<String> geohashes,
            Pageable pageable
    );



}

