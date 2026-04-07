package com.example.Product.Repository;

import com.example.Product.Model.ProductData.Product_Variants;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantsRepo extends JpaRepository<Product_Variants,String> {

    @Modifying
    @Transactional
    @Query(""" 
                UPDATE Product_Variants p
                SET p.quantity = p.quantity - 1
                WHERE p.variant_id = :productId
                  AND p.quantity >= :requestedQty
            """)
    int subtractIfAvailable(@Param("productId") String productId, @Param("requestedQty") Integer quantity);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Product_Variants p
            SET p.quantity = p.quantity + 1
            WHERE p.variant_id = :productId
            """)
    int addProductAgain(@Param("productId") String productId);

}

