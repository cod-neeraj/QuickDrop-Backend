package com.example.User.Repository.CustomerRepositorys;

import com.example.User.Models.Customer.Customers;
import com.example.User.Models.OrdersData.MainOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepo  extends JpaRepository<Customers,Long> {

    Optional<Customers> findByPhoneNumber(String phoneNumber);

    @Query("SELECT c.name FROM Customers c WHERE c.phoneNumber =:phoneNumber")
    String findByPhone(@Param("phoneNumber") String phoneNumber);


    @Query("SELECT c.userId FROM Customers c WHERE c.phoneNumber =:phoneNumber")
    String findByPhoneUserId(@Param("phoneNumber") String phoneNumber);

}
