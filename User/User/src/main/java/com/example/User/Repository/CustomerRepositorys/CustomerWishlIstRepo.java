package com.example.User.Repository.CustomerRepositorys;


import com.example.User.DataToShow.WishlistAddData;
import com.example.User.Models.Customer.Customer_WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerWishlIstRepo extends JpaRepository<Customer_WishList,Long> {

    @Query("""
            SELECT new com.example.User.DataToShow.WishlistAddData(w.productId,w.name,w.type,w.brand,w.defaultImage)
            FROM Customer_WishList w WHERE w.customer.phoneNumber =:phoneNumber
          """)
    List<WishlistAddData> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}