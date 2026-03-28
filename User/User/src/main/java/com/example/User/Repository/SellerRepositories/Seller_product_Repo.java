package com.example.User.Repository.SellerRepositories;

import com.example.User.DataToShow.SellerDashBoard.BasicProductDetails;
import com.example.User.Models.Seller.Seller_product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface Seller_product_Repo extends JpaRepository<Seller_product,Long> {

    @Query("SELECT COUNT(p) FROM Seller_product p WHERE p.seller.id = :sellerId")
    Integer countProductBySellerId(@Param("sellerId") Long sellerId);

    @Query("""
    SELECT new com.example.User.DataToShow.SellerDashBoard.BasicProductDetails(
        p.productId, p.productName, p.price, p.productCategory, p.quantity
    )
    FROM Seller_product p WHERE p.seller.id = :sellerId AND p.quantity <= 10
""")
    Set<BasicProductDetails> findLowStock(@Param("sellerId") Long sellerId);

    @Query("SELECT new com.example.User.DataToShow.SellerDashBoard.BasicProductDetails(" +
            "p.productId, p.productName, p.price, p.productCategory, p.quantity) " +
            "FROM Seller_product p " +
            "WHERE p.seller.phoneNumber = :sellerId")
    Page<BasicProductDetails> findProductSet(@Param("sellerId") String sellerId, Pageable pageable);

}
