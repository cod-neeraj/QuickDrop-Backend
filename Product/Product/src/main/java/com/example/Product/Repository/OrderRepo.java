package com.example.Product.Repository;

import com.example.Product.Model.ProductData.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Orders,String> {

    List<Orders> findByPaymentId(String id);
}
