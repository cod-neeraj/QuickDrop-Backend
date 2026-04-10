package com.example.Product.Repository;

import com.example.Product.DTO.LowStockDTO;
import com.example.Product.DTO.RecommendedProductCard;
import com.example.Product.Model.ProductData.Product;
import com.example.Product.Model.ProductData.ProductCategory;
import com.example.Product.Model.ProductSearchAbleObject;
//import ;
import com.example.Product.Service.SellerProductList;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product,String> {

    @Query(value = """
            SELECT
                (p.attributes ->> 'id') AS id,
                (p.attributes ->> 'name') AS name,
                (p.attributes ->> 'type') AS type,
                (p.attributes ->> 'brand') AS brand,
                (p.attributes ->> 'category') AS category,
                (p.attributes ->> 'rating')::float AS rating,
                (p.attributes ->> 'quantity')::int AS quantity,
                (p.attributes ->> 'image') AS imageUrl,
                ROUND((ts_rank_cd(p.tsvector_column, to_tsquery(:tsQuery)) * 100)::numeric, 2)::float AS match_percentage
            FROM product.product_search_view p
            LEFT JOIN product.product_stats ps ON ps.product_id = p.product_id
            WHERE
                (
                    :tsQuery = ''
                    OR p.tsvector_column @@ to_tsquery(:tsQuery)
                )
                AND (
                    :maxDistance IS NULL
                    OR ST_DWithin(
                        p.location,
                        ST_MakePoint(:userLng, :userLat)::geography,
                        :maxDistance * 1000
                    )
                )
                AND (
                    (:minPrice IS NULL OR p.price::float >= :minPrice)
                )
                AND (
                    (:maxPrice IS NULL OR  p.price::float <= :maxPrice)
                )
            ORDER BY
                ps.score DESC NULLS LAST,
                match_percentage DESC,
                p.product_id
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM product.product_search_view p
                    LEFT JOIN product.product_stats ps ON ps.product_id = p.product_id
                    WHERE
                        (
                            :tsQuery = ''
                            OR p.tsvector_column @@ to_tsquery(:tsQuery)
                        )
                        AND (
                            :maxDistance IS NULL
                            OR ST_DWithin(
                                p.location,
                                ST_MakePoint(:userLng, :userLat)::geography,
                                :maxDistance * 1000
                            )
                        )
                        AND (
                            (:minPrice IS NULL OR  p.price::float >= :minPrice)
                        )
                        AND (
                            (:maxPrice IS NULL OR p.price::float <= :maxPrice)
                        )
                    """,
            nativeQuery = true
    )
    Page<ProductSearchAbleObject> searchProducts(
            @Param("tsQuery") String tsQuery,
            @Param("userLng") double userLng,
            @Param("userLat") double userLat,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("maxDistance") Double maxDistance,
            Pageable pageable
    );


    @Query("SELECT p.name FROM Product p WHERE p.shopkeeperId=:id AND p.name=:name")
    Optional<String> checkDuplicate(@Param("id") String id, @Param("name") String name);

    @Query(" SELECT p FROM Product p  WHERE p.id=:id")
    Product findProduct(@Param("id") String id);

    @Modifying
    @Transactional
    @Query(""" 
                UPDATE Product p
                SET p.quantity = p.quantity - 1
                WHERE p.productId = :productId
                  AND p.quantity >= :requestedQty
            """)
    int subtractIfAvailable(@Param("productId") String productId, @Param("requestedQty") Integer quantity);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Product p
            SET p.quantity = p.quantity + 1
            WHERE p.productId = :productId
            """)
    int addProductAgain(@Param("productId") String productId);


    @Query("SELECT new com.example.Product.DTO.RecommendedProductCard(p.productId,p.name,p.type,p.brand,p.defaultImageUrl) FROM Product p JOIN p.productStats ps WHERE p.geohash IN :geohashes ORDER BY ps.score DESC")
    List<RecommendedProductCard> findBestProduct(@Param("geohashes") List<String> geohashes);


    /*
    public class SellerProductList {
    private String productId;
    private String name;
    private String type;
    private String brand;
    private ProductCategory productCategory;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String defaultImageUrl;
    private Double defaultPrice;     */

    @Query("""
            SELECT new com.example.Product.Service.SellerProductList(s.productId,s.name,s.type,s.brand,s.productCategory,s.createdAt,s.updatedAt,s.defaultImageUrl,s.defaultPrice)
            FROM Product s WHERE s.phoneNumber=:phoneNumber
            """)
    Page<SellerProductList> findAllProductByPhoneNumber(@Param("phoneNumber") String phoneNumber,
                                                        Pageable pageable);

    @Query("""
             SELECT COUNT(p)
             FROM Product p
             WHERE p.phoneNumber = :phoneNumber
            """)
    Long findProductCount(@Param("phoneNumber") String phoneNumber);


    @Query("""
             SELECT new com.example.Product.DTO.LowStockDTO(p.productId,p.defaultImageUrl,p.name,p.brand,pv.quantity)
             FROM Product p
             LEFT JOIN p.productVariants pv
             WHERE p.phoneNumber = :phoneNumber AND pv.quantity<15
            """)
    List<LowStockDTO> findLowStockProduct(@Param("phoneNumber") String phoneNumber);


}




/*

   AND (
            COALESCE(:brands, ARRAY[]::varchar[]) = ARRAY[]::varchar[]
            OR (p.attributes ->> 'brand') = ANY(CAST(:brands AS varchar[]))
        )
 */

/*

        AND (
            COALESCE(:colors, ARRAY[]::varchar[]) = ARRAY[]::varchar[]
            OR (p.attributes ->> 'color') = ANY(CAST(:colors AS varchar[]))
        )
 */

//AND (
//            COALESCE(:brands, ARRAY[]::varchar[]) = ARRAY[]::varchar[]
//            OR (p.attributes ->> 'brand') = ANY(CAST(:brands AS varchar[]))
//        )
