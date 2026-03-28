package com.example.User.Repository.OrderRepositories;

import com.example.User.Models.OrdersData.ProductDetailsInfo;
import com.sun.jdi.LongValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInfoRepo  extends JpaRepository<ProductDetailsInfo, Long> {
}
