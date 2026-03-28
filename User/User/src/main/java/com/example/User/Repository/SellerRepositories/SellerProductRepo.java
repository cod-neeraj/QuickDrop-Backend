package com.example.User.Repository.SellerRepositories;

import com.example.User.DataToShow.SellerDashBoard.BasicProductDetails;
import com.example.User.Models.Seller.Seller_product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SellerProductRepo extends JpaRepository<Seller_product,String> {

   @Query("SELECT new com.example.User.DataToShow.SellerDashBoard.BasicProductDetails(p.productId,p.productName,p.price,p.productCategory,p.quantity) " +
           "FROM Seller_product p where p.seller.phoneNumber = :phoneNumber AND p.quantity<=10")
   Set<BasicProductDetails> findLowStock(@Param("phoneNumber") String phoneNumber);
}
